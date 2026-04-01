package cz.cvut.fel.nss.booking.service;

import cz.cvut.fel.nss.booking.Booking;
import cz.cvut.fel.nss.booking.dao.BookingDao;
import cz.cvut.fel.nss.booking.dto.BookingDto;
import cz.cvut.fel.nss.booking.dto.mapper.BookingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;


@Service
@Transactional
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingDao bookingDao;
    private final TourService tourService;
    private final ReservationDao reservationDao;
    private final BookingMapper bookingMapper;
    private final TourDao tourDao;
    private final AccommodationDao accommodationDao;
    private final PersonDao personDao;



    public BookingService(BookingDao bookingDao,
                          TourService tourService,
                          ReservationDao reservationDao,
                          BookingMapper bookingMapper,
                          TourDao tourDao,
                          AccommodationDao accommodationDao,
                          PersonDao personDao) {
        this.bookingDao = bookingDao;
        this.tourService = tourService;
        this.reservationDao = reservationDao;
        this.bookingMapper = bookingMapper;
        this.tourDao = tourDao;
        this.accommodationDao = accommodationDao;
        this.personDao = personDao;
    }

    public Booking createBookingFromDto(BookingDto dto) {
        Objects.requireNonNull(dto);

        if (dto.id() != null) {
            throw new IllegalArgumentException("Booking id must be null on create");
        }
        if (dto.reservationNumber() != null) {
            throw new IllegalArgumentException("Booking reservationNumber must be null on create");
        }

        final Long tourId = Optional.ofNullable(dto.tour())
                .map(TourDto::id)
                .orElse(null);
        if (tourId == null) {
            throw new IllegalArgumentException("Booking must have tour.id");
        }

        if (dto.persons() == null || dto.persons().isEmpty()) {
            throw new IllegalArgumentException("Booking must have at least one person");
        }
        if (dto.reservations() == null || dto.reservations().isEmpty()) {
            throw new IllegalArgumentException("Booking must have at least one reservation");
        }

        final Tour tour = tourDao.find(tourId);
        if (tour == null) {
            throw new NotFoundException("Tour not found: " + tourId);
        }

        final Booking booking = new Booking();
        booking.setTour(tour);
        booking.setCreatedAt(LocalDate.now());


        final List<Person> persons = foundOrCreatePersons(dto.persons());
        booking.setPersons(persons);
        for (Person person : persons) {
            person.getBookings().add(booking);
        }

        // tour capacity validation
        int requestedSize = booking.getPersons().size();
        tourService.validateCapacity(tour, requestedSize);

        final List<Reservation> reservations = createReservations(dto.reservations(), booking);
        booking.setReservations(reservations);
        // accommodation validation + required reservation fields + bidirectional link
        for (Reservation reservation : reservations) {
            validationForReservation(booking, reservation);
        }

        if (booking.getReservationNumber() <= 0) {
            booking.setReservationNumber(bookingDao.nextReservationNumber());
        }

        log.info("Total price:\n{}", booking.priceReport());
        booking.saveTotalPrice();
        bookingDao.save(booking);

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
        List<Booking> bookings=bookingDao.findBookingsCreatedBetween(fromDate, toDate);
        List<BookingDto> bookingDtos=new ArrayList<>();
        for (Booking booking:bookings) {
            bookingDtos.add(bookingMapper.bookingToBookingDto(booking));
        }
        return bookingDtos;
    }


    public void removeBookingByTour(String destination, LocalDate startDate) {
        Tour tour= tourDao.findByDestinationAndStartDate(destination, startDate);
        List<Booking> copy_bookings=new ArrayList<>(tour.getBookings());

        for (Booking booking:copy_bookings) {
            tour.removeBooking(booking);
            bookingDao.remove(booking);
        }
    }

    public void validateCapacity(Tour tour, int requestedSize) {
        Objects.requireNonNull(tour);
        int capacity = tour.getCapacity();
        int occupied= bookingDao.countPersonsByTour(tour.getId());

        if(requestedSize+occupied>capacity) {
            throw new IllegalStateException(
                    "Tour capacity exceeded: capacity=" + capacity
            );
        }
    }








    }




