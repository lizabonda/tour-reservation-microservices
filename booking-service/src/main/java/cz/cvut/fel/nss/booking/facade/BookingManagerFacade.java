package cz.cvut.fel.nss.booking.facade;

import cz.cvut.fel.nss.booking.entity.TourStatus;
import cz.cvut.fel.nss.booking.service.BookingPricingService;
import cz.cvut.fel.nss.booking.client.AccommodationClient;
import cz.cvut.fel.nss.booking.client.TourClient;
import cz.cvut.fel.nss.booking.client.UserClient;
import cz.cvut.fel.nss.booking.dto.accommodation.AccommodationPricingSummaryDto;
import cz.cvut.fel.nss.booking.dto.accommodation.ReservationDto;
import cz.cvut.fel.nss.booking.dto.booking.CreateBookingDTO;
import cz.cvut.fel.nss.booking.dto.tour.TourDto;
import cz.cvut.fel.nss.booking.dto.user.PersonDto;
import cz.cvut.fel.nss.exception.NotFoundException;
import cz.cvut.fel.nss.booking.entity.Booking;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingManagerFacade {

    private final TourClient tourClient;
    private final UserClient userClient;
    private final AccommodationClient accommodationClient;
    private final BookingPricingService bookingPricingService;

    public BookingManagerFacade(TourClient tourClient, UserClient userClient, 
                                AccommodationClient accommodationClient, 
                                BookingPricingService bookingPricingService) {
        this.tourClient = tourClient;
        this.userClient = userClient;
        this.accommodationClient = accommodationClient;
        this.bookingPricingService = bookingPricingService;
    }

    public Booking initializeBooking(CreateBookingDTO dto) {
        // 1. Tour validation
        final TourDto tour = tourClient.getTour(dto.tourId());
        if (tour == null) {
            throw new NotFoundException("Tour not found: " + dto.tourId());
        }
        if (tour.status() != TourStatus.ACTIVE) {
            throw new IllegalStateException("Tour is not active");
        }

        // 2. User/Person management
        List<PersonDto> personsDtoInput = dto.persons().stream()
                .map(p -> new PersonDto(p.id(), p.firstName(), p.lastName(), p.dateOfBirth()))
                .collect(Collectors.toList());
        List<PersonDto> personsCreated = userClient.findOrCreatePersons(personsDtoInput);
        List<Long> personIds = personsCreated.stream().map(PersonDto::id).collect(Collectors.toList());

        // 3. Capacity validation
        int requestedSize = personIds.size();
        if (requestedSize > tour.capacity()) {
            throw new IllegalStateException("Tour capacity exceeded: capacity=" + tour.capacity());
        }

        // 4. Price calculation via accommodation service
        List<ReservationDto> resDtoInput = dto.reservations().stream()
                .map(r -> new ReservationDto(null, r.startDate(), r.endDate(), 0, r.accommodationId(), null, requestedSize))
                .collect(Collectors.toList());

        AccommodationPricingSummaryDto pricing = accommodationClient.calculatePrice(resDtoInput);

        // 5. Final price calculation
        double totalPrice = bookingPricingService.totalPrice(
                LocalDate.now(),
                tour.startDate(),
                tour.price() * requestedSize,
                pricing.accommodationPrice(),
                pricing.allInclusiveCharge()
        );

        // Create Booking entity and populate it with data
        Booking booking = new Booking();
        booking.setTourId(tour.id());
        booking.setCreatedAt(LocalDate.now());
        booking.setPersonIds(personIds);
        booking.setTotalPrice(totalPrice);

        return booking;
    }

    public void finalizeBooking(Booking booking, CreateBookingDTO dto) {
        int requestedSize = booking.getPersonIds().size();
        List<ReservationDto> resDtoInput = dto.reservations().stream()
                .map(r -> new ReservationDto(null, r.startDate(), r.endDate(), 0, r.accommodationId(), null, requestedSize))
                .collect(Collectors.toList());

        try {
            List<ReservationDto> createdReservations = accommodationClient.createReservations(resDtoInput, booking.getId());
            List<Long> reservationIds = createdReservations.stream().map(ReservationDto::id).collect(Collectors.toList());
            booking.setReservationIds(reservationIds);
        } catch (feign.FeignException.Conflict e) {
            throw new IllegalStateException("Accommodation is not available for given dates", e);
        }
    }

}
