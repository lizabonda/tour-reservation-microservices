package cz.cvut.fel.nss.booking.service;

import cz.cvut.fel.nss.booking.BookingPricingService;
import cz.cvut.fel.nss.booking.dto.accommodation.AccommodationPricingSummaryDto;
import cz.cvut.fel.nss.booking.dto.accommodation.ReservationDto;
import cz.cvut.fel.nss.booking.dto.booking.*;
import cz.cvut.fel.nss.booking.dto.tour.TourDto;
import cz.cvut.fel.nss.booking.dto.user.PersonDto;

import cz.cvut.fel.nss.booking.facade.BookingManagerFacade;
import cz.cvut.fel.nss.entity.Booking;
import cz.cvut.fel.nss.booking.client.AccommodationClient;
import cz.cvut.fel.nss.booking.client.TourClient;
import cz.cvut.fel.nss.booking.client.UserClient;
import cz.cvut.fel.nss.booking.dao.BookingDao;
import cz.cvut.fel.nss.booking.dto.mapper.BookingMapper;
import cz.cvut.fel.nss.booking.exception.NotFoundException;
import cz.cvut.fel.nss.booking.state.BookingStateFactory;
import cz.cvut.fel.nss.entity.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final BookingManagerFacade bookingManagerFacade;

    public BookingService(BookingDao bookingDao,
                          BookingMapper bookingMapper,
                          KafkaTemplate<String, Object> kafkaTemplate,
                          BookingManagerFacade bookingManagerFacade) {
        this.bookingDao = bookingDao;
        this.bookingMapper = bookingMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.bookingManagerFacade = bookingManagerFacade;
    }

    public Booking createBookingFromDto(CreateBookingDTO dto) {
        validateCreateBookingDto(dto);

        // Use Facade to prepare the Booking entity
        final Booking booking = bookingManagerFacade.initializeBooking(dto);

//        if (booking.getBookingNumber() == null || booking.getBookingNumber() <= 0) {
//            booking.setBookingNumber(bookingDao.nextBookingNumber());
//        }

        // 1. First save to DB so booking has ID
        bookingDao.save(booking);

        // 2. Then finalize (external calls to accommodation service using valid booking ID)
        bookingManagerFacade.finalizeBooking(booking, dto);

        // 3. Update booking to save reservationIds
        bookingDao.update(booking);

        // 4. Last step - notify Kafka about tour capacity change
        BookingEvent event = new BookingEvent(booking.getId(), booking.getTourId(), -booking.getPersonIds().size());
        kafkaTemplate.send("tour-capacity", event);
        log.info("Booking created with id: {}, total price: {}", booking.getId(), booking.getTotalPrice());

        return booking;
    }

    private void validateCreateBookingDto(CreateBookingDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("CreateBookingDTO must not be null");
        }
        if (dto.tourId() == null) {
            throw new IllegalArgumentException("Booking must have tourId");
        }
        if (dto.persons() == null || dto.persons().isEmpty()) {
            throw new IllegalArgumentException("Booking must have at least one person");
        }
        if (dto.reservations() == null || dto.reservations().isEmpty()) {
            throw new IllegalArgumentException("Booking must have at least one reservation");
        }

        for (BookingPersonDTO person : dto.persons()) {
            if (person == null) {
                throw new IllegalArgumentException("Person must not be null");
            }
            if (person.id() == null) {
                if (person.firstName() == null || person.firstName().isBlank()) {
                    throw new IllegalArgumentException("Person firstName must not be null or blank");
                }
                if (person.lastName() == null || person.lastName().isBlank()) {
                    throw new IllegalArgumentException("Person lastName must not be null or blank");
                }
                if (person.dateOfBirth() == null) {
                    throw new IllegalArgumentException("Person dateOfBirth must not be null");
                }
                if (person.dateOfBirth().isAfter(LocalDate.now())) {
                    throw new IllegalArgumentException("Person dateOfBirth cannot be in the future");
                }
            }
        }

        for (BookingReservationDTO res : dto.reservations()) {
            if (res == null) {
                throw new IllegalArgumentException("Reservation must not be null");
            }
            if (res.accommodationId() == null) {
                throw new IllegalArgumentException("Reservation accommodationId must not be null");
            }
            if (res.startDate() == null) {
                throw new IllegalArgumentException("Reservation startDate must not be null");
            }
            if (res.endDate() == null) {
                throw new IllegalArgumentException("Reservation endDate must not be null");
            }
            if (res.startDate().isAfter(res.endDate())) {
                throw new IllegalArgumentException("Reservation startDate cannot be after endDate");
            }
        }
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
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Invalid date range");
        }
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        List<Booking> bookings = bookingDao.findBookingsCreatedBetween(fromDate, toDate);
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(bookingMapper.bookingToBookingDto(booking));
        }
        return bookingDtos;
    }


    public void cancelBookingByTour(Long tourId) {

        List<Booking> bookings = bookingDao.findAllByTour(tourId);
        List<BookingEvent> events = new ArrayList<>();

        for (Booking booking : bookings) {
            if (booking.getStatus() != BookingStatus.CANCELLED) {
                BookingEvent event = new BookingEvent(booking.getId(), tourId, booking.getPersonIds().size());
                events.add(event);

                BookingStateFactory.getState(booking.getStatus()).cancel(booking);
                bookingDao.update(booking);
            }
        }

        // Send all notifications at the end after DB updates
        for (BookingEvent event : events) {
            kafkaTemplate.send("booking-cancelled", event);
        }
//        List<Booking> bookings = bookingDao.findAllByTour(tourId);
//        for (Booking booking : bookings) {
//            if (booking.getStatus() != BookingStatus.CANCELLED) {
//                BookingEvent event= new BookingEvent(booking.getId(), tourId,booking.getPersonIds().size());
//                kafkaTemplate.send("booking-cancelled", event);
//                try {
//                    accommodationClient.cancelReservationsByBookingId(booking.getId());
//                } catch (feign.FeignException.NotFound e) {
//                    log.warn("Reservations for booking {} not found during cancellation", booking.getId());
//                }
//                BookingStateFactory.getState(booking.getStatus()).cancel(booking);
//                bookingDao.update(booking);
//            }
//        }
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
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return;
        }
        BookingStateFactory.getState(booking.getStatus()).cancel(booking);
        bookingDao.update(booking);
        BookingEvent event= new BookingEvent(booking.getId(),booking.getTourId(), booking.getPersonIds().size());
        kafkaTemplate.send("tour-capacity", event);
    }

    public void cancelBookingByUser(Long id) {
        Booking booking = bookingDao.find(id);
        if (booking == null) {
            return;
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return;
        }

        bookingDao.update(booking);
        int personsCount = booking.getPersonIds().size();
        BookingEvent event= new BookingEvent(booking.getId(), booking.getTourId(),  personsCount);

        kafkaTemplate.send("tour-capacity", event);
        BookingStateFactory.getState(booking.getStatus()).cancel(booking);

//        tourClient.updateCapacity(booking.getTourId(), booking.getPersonIds().size());
    }
}




