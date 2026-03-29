package cz.cvut.fel.nss.projekt.dao;

import cz.cvut.fel.ear.projekt.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@ComponentScan(basePackages = "cz.cvut.fel.ear.projekt.dao")
@Transactional
class BookingDaoTest {

    @Autowired
    private BookingDao bookingDao;

    @Autowired
    private TourDao tourDao;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private AccommodationDao accommodationDao;

    @Test
    void findById_ShouldReturnSavedBooking() {
        Tour tour = new Tour();
        tour.setTitle("Inline Greece Tour");
        tour.setDescription("Tour created inside DAO test");
        tour.setDestination("Greece");
        tour.setCapacity(10);
        tour.setPrice(500.0);
        tour.setStartDate(LocalDate.now().plusDays(50));
        tour.setEndDate(LocalDate.now().plusDays(57));
        tourDao.save(tour);

        Person person = new Person();
        person.setFirstName("Alice");
        person.setLastName("LastName");
        person.setDateOfBirth(LocalDate.of(1995, 5, 5));
        personDao.save(person);

        Accommodation acc = new Accommodation();
        acc.setName("Name Hotel");
        acc.setAddress("Adress Street 1");
        acc.setCity("Prague");
        acc.setCapacity(2);
        acc.setMealPlan(MealPlan.ALL_INCLUSIVE);
        acc.setPricePerNight(100.0);
        acc.setStars(4);
        acc.setRoomType("Single");
        accommodationDao.save(acc);

        Reservation res = new Reservation();
        res.setStartDate(LocalDateTime.now().plusDays(50));
        res.setEndDate(LocalDateTime.now().plusDays(53));
        res.setReservationPrice(300.0);
        res.setAccommodation(acc);

        Booking booking = new Booking();
        booking.setReservationNumber(999);
        booking.setCreatedAt(LocalDate.now());
        booking.setTour(tour);
        booking.setTotalPrice(800.0);
        booking.setPersons(List.of(person));
        booking.setReservations(List.of(res));
        res.setBooking(booking);

        bookingDao.save(booking);

        Optional<Booking> loaded = Optional.ofNullable(bookingDao.find(booking.getId()));
        assertTrue(loaded.isPresent(), "Booking should be saved and retrievable");

        Booking b = loaded.get();
        assertNotNull(b.getTour(), "Booking should have linked Tour");
        assertEquals(999, b.getReservationNumber());
        assertEquals(800.0, b.getTotalPrice());
        assertEquals(1, b.getPersons().size(), "Booking should have one linked Person");
        assertEquals(1, b.getReservations().size(), "Booking should have one Reservation");
    }

    @Test
    void persist_ShouldSaveBookingWithPersonAndTour() {
        Tour tour = new Tour();
        tour.setTitle("Test Tour");
        tour.setDescription("");
        tour.setDestination("Italy");
        tour.setCapacity(5);
        tour.setPrice(600.0);
        tour.setStartDate(LocalDate.now().plusDays(40));
        tour.setEndDate(LocalDate.now().plusDays(47));
        tourDao.save(tour);

        Person person = new Person();
        person.setFirstName("Bob");
        person.setLastName("Tester");
        person.setDateOfBirth(LocalDate.of(1990, 1, 1));
        personDao.save(person);

        Booking booking = new Booking();
        booking.setTour(tour);
        booking.setPersons(List.of(person));
        booking.setReservationNumber(2001);
        booking.setCreatedAt(LocalDate.now());
        booking.setTotalPrice(900.0);
        bookingDao.save(booking);

        assertNotNull(booking.getId(), "Booking ID should be assigned");
        assertEquals("Italy", booking.getTour().getDestination());
        assertEquals(1, booking.getPersons().size());
    }
}
