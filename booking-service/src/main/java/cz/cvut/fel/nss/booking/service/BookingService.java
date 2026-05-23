package cz.cvut.fel.nss.booking.service;

import cz.cvut.fel.nss.booking.BookingPricingService;
import cz.cvut.fel.nss.booking.dto.accommodation.AccommodationPricingSummaryDto;
import cz.cvut.fel.nss.booking.dto.accommodation.ReservationDto;
import cz.cvut.fel.nss.booking.dto.booking.BookingEvent;
import cz.cvut.fel.nss.booking.dto.booking.BookingDto;
import cz.cvut.fel.nss.booking.dto.booking.CreateBookingDTO;
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

        // Use Facade to prepare the Booking entity
        final Booking booking = bookingManagerFacade.initializeBooking(dto);

        if (booking.getBookingNumber() <= 0) {
            booking.setBookingNumber(bookingDao.nextBookingNumber());
        }

        bookingDao.save(booking);

        bookingManagerFacade.finalizeBooking(booking, dto);
        bookingDao.update(booking);

        BookingEvent event = new BookingEvent(booking.getId(), booking.getTourId(), -booking.getPersonIds().size());
        kafkaTemplate.send("tour-capacity", event);
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
                BookingEvent event= new BookingEvent(booking.getId(), tourId,booking.getPersonIds().size());
                kafkaTemplate.send("booking-cancelled", event);
//                try {
//                    accommodationClient.cancelReservationsByBookingId(booking.getId());
//                } catch (feign.FeignException.NotFound e) {
//                    log.warn("Reservations for booking {} not found during cancellation", booking.getId());
//                }
                BookingStateFactory.getState(booking.getStatus()).cancel(booking);
                bookingDao.update(booking);
            }
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

        int personsCount = booking.getPersonIds().size();
        BookingEvent event= new BookingEvent(booking.getId(), booking.getTourId(),  personsCount);

        kafkaTemplate.send("tour-capacity", event);
        BookingStateFactory.getState(booking.getStatus()).cancel(booking);
        bookingDao.update(booking);
//        tourClient.updateCapacity(booking.getTourId(), booking.getPersonIds().size());
    }
}




