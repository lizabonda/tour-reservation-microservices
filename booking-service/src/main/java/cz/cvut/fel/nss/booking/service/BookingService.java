package cz.cvut.fel.nss.booking.service;

import cz.cvut.fel.nss.booking.dto.accommodation.AccommodationPricingSummaryDto;
import cz.cvut.fel.nss.booking.dto.accommodation.ReservationDto;
import cz.cvut.fel.nss.booking.dto.tour.TourDto;
import cz.cvut.fel.nss.booking.dto.user.PersonDto;

import cz.cvut.fel.nss.entity.Booking;
import cz.cvut.fel.nss.booking.client.AccommodationClient;
import cz.cvut.fel.nss.booking.client.TourClient;
import cz.cvut.fel.nss.booking.client.UserClient;
import cz.cvut.fel.nss.booking.dao.BookingDao;
import cz.cvut.fel.nss.booking.dto.*;
import cz.cvut.fel.nss.booking.dto.mapper.BookingMapper;
import cz.cvut.fel.nss.booking.exception.NotFoundException;
import cz.cvut.fel.nss.entity.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Transactional
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingDao bookingDao;
    private final BookingMapper bookingMapper;
    private final TourClient tourClient;
    private final UserClient userClient;
    private final AccommodationClient accommodationClient;


    public BookingService(BookingDao bookingDao,
                          BookingMapper bookingMapper,
                          TourClient tourClient,
                          UserClient userClient,
                          AccommodationClient accommodationClient) {
        this.bookingDao = bookingDao;
        this.bookingMapper = bookingMapper;
        this.tourClient = tourClient;
        this.userClient = userClient;
        this.accommodationClient = accommodationClient;
    }

    public Booking createBookingFromDto(CreateBookingDTO dto) {
        Objects.requireNonNull(dto);

        if (dto.tourId() == null) {
            throw new IllegalArgumentException("Booking must have tourId");
        }

        if (dto.persons() == null || dto.persons().isEmpty()) {
            throw new IllegalArgumentException("Booking must have at least one person");
        }
        if (dto.reservations() == null || dto.reservations().isEmpty()) {
            throw new IllegalArgumentException("Booking must have at least one reservation");
        }

        final TourDto tour = tourClient.getTour(dto.tourId());
        if (tour == null) {
            throw new NotFoundException("Tour not found: " + dto.tourId());
        }

        final Booking booking = new Booking();
        booking.setTourId(tour.id());
        booking.setCreatedAt(LocalDate.now());

        // find or create persons via user-service
        List<PersonDto> personsDtoInput = dto.persons().stream()
                .map(p -> new PersonDto(p.id(), p.firstName(), p.lastName(), p.dateOfBirth()))
                .collect(Collectors.toList());
        List<PersonDto> personsCreated = userClient.findOrCreatePersons(personsDtoInput);
        booking.setPersonIds(personsCreated.stream().map(PersonDto::id).collect(Collectors.toList()));

        // tour capacity validation
        int requestedSize = booking.getPersonIds().size();
        if (requestedSize > tour.capacity()) {
            throw new IllegalStateException("Tour capacity exceeded: capacity=" + tour.capacity());
        }

        // Calculate price and create reservations via accommodation-service
        List<ReservationDto> resDtoInput = dto.reservations().stream()
                .map(r -> new ReservationDto(null, r.startDate(), r.endDate(), 0, r.accommodationId(), null, requestedSize))
                .collect(Collectors.toList());

        AccommodationPricingSummaryDto pricing = accommodationClient.calculatePrice(resDtoInput);
        double totalPrice = tour.price() * requestedSize + pricing.accommodationPrice() + pricing.allInclusiveCharge();
        booking.setTotalPrice(totalPrice);

        if (booking.getBookingNumber() <= 0) {
            booking.setBookingNumber(bookingDao.nextBookingNumber());
        }

        bookingDao.save(booking);

        // Link reservations to bookingId
        List<ReservationDto> createdReservations;
        try {
            createdReservations = accommodationClient.createReservations(resDtoInput, booking.getId());
        } catch (feign.FeignException.Conflict e) {
            throw new IllegalStateException("Accommodation is not available for given dates", e);
        }

        booking.setReservationIds(createdReservations.stream().map(ReservationDto::id).collect(Collectors.toList()));
        bookingDao.update(booking);

        tourClient.updateCapacity(tour.id(), -requestedSize);
        log.info("Booking created with id: {}, total price: {}", booking.getId(), booking.getTotalPrice());

        return booking;
    }

    public Booking findById(Long id) {
        Objects.requireNonNull(id);
        final Booking booking = bookingDao.find(id);
        if (booking == null) {
            throw new NotFoundException("Booking not found: " + id);
        }
        return booking;
    }

    public List<BookingDto> getBookingsCreatedBetween(LocalDate fromDate, LocalDate toDate) {
        List<Booking> bookings = bookingDao.findBookingsCreatedBetween(fromDate, toDate);
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(bookingMapper.bookingToBookingDto(booking));
        }
        return bookingDtos;
    }


    public void cancelBookingByTour(Long tourId) {
        List<Booking> bookings = bookingDao.findAllByTour(tourId);
        for (Booking booking : bookings) {
            if (booking.getStatus() != BookingStatus.CANCELLED) {
                try {
                    accommodationClient.cancelReservationsByBookingId(booking.getId());
                } catch (feign.FeignException.NotFound e) {
                    log.warn("Reservations for booking {} not found during cancellation", booking.getId());
                }
            }
            booking.setStatus(BookingStatus.CANCELLED);
            bookingDao.update(booking);
        }
        }


    public List<BookingDto> findByUser(Long userId) {
        List<Booking> bookings = bookingDao.findByUser(userId);
        return bookings.stream().map(b -> bookingMapper.bookingToBookingDto(b)).collect(Collectors.toList());

    }
    // when we delete accommodation
    public void removeBookingByIdBySystem (Long id) {
        Booking booking = bookingDao.find(id);
        if (booking == null) {
            throw new NotFoundException("Booking not found: " + id);
        }
        if(booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingDao.update(booking);
        tourClient.updateCapacity(booking.getTourId(), booking.getPersonIds().size());
    }

    public void cancelBookingByUser(Long id) {
        Booking booking = bookingDao.find(id);
        if (booking == null || booking.getStatus() == BookingStatus.CANCELLED) {
            return;
        }
        try {
            accommodationClient.cancelReservationsByBookingId(id);
        } catch (feign.FeignException.NotFound e) {
            log.warn("Reservations for booking {} not found during cancellation by user", id);
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingDao.update(booking);
        tourClient.updateCapacity(booking.getTourId(), booking.getPersonIds().size());
    }
}




