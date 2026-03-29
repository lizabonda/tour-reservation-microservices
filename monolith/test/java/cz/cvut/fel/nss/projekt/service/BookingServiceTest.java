package cz.cvut.fel.nss.projekt.service;

import cz.cvut.fel.nss.projekt.dao.BookingDao;
import cz.cvut.fel.nss.projekt.dao.PersonDao;
import cz.cvut.fel.nss.projekt.dao.ReservationDao;
import cz.cvut.fel.nss.projekt.dao.TourDao;
import cz.cvut.fel.nss.projekt.dao.AccommodationDao;
import cz.cvut.fel.nss.projekt.dto.AccommodationDto;
import cz.cvut.fel.nss.projekt.dto.BookingDto;
import cz.cvut.fel.nss.projekt.dto.PersonDto;
import cz.cvut.fel.nss.projekt.dto.ReservationDto;
import cz.cvut.fel.nss.projekt.dto.TourDto;
import cz.cvut.fel.nss.projekt.dto.mapper.BookingMapper;
import cz.cvut.fel.ear.projekt.model.*;
import cz.cvut.fel.nss.projekt.model.Accommodation;
import cz.cvut.fel.nss.projekt.model.Booking;
import cz.cvut.fel.nss.projekt.model.Person;
import cz.cvut.fel.nss.projekt.model.Tour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class BookingServiceTest {

    @Mock
    private BookingDao bookingDao;

    @Mock
    private TourService tourService;

    @Mock
    private ReservationDao reservationDao;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private TourDao tourDao;

    @Mock
    private AccommodationDao accommodationDao;

    @Mock
    private PersonDao personDao;

    @InjectMocks
    private BookingService bookingService;

    private Tour tour;
    private Person person;
    private Accommodation accommodation;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        tour = new Tour();
        tour.setId(1L);
        tour.setCapacity(10);
        tour.setPrice(500.0);
        tour.setTitle("Summer");
        tour.setStartDate(LocalDate.now().plusDays(30));
        tour.setEndDate(LocalDate.now().plusDays(37));

        person = new Person();
        person.setFirstName("Alice");
        person.setLastName("Tester");
        person.setDateOfBirth(LocalDate.of(1990, 7, 13));

        accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setPricePerNight(100.0);
    }

    private PersonDto samplePersonDto() {
        return new PersonDto(
                1L,
                person.getFirstName(),
                person.getLastName(),
                person.getDateOfBirth()
        );
    }

    private ReservationDto sampleReservationDto() {
        return new ReservationDto(
                null,
                LocalDate.now().plusDays(50),
                LocalDate.now().plusDays(53),
                0.0,
                new AccommodationDto(
                        accommodation.getId(),
                        null,
                        null,
                        accommodation.getPricePerNight(),
                        null
                ),
                null
        );
    }

    @Test
    void saveBooking_ShouldThrowException_WhenBookingHasNoTour() {
        BookingDto dto = new BookingDto(
                null,
                null,
                null,
                List.of(samplePersonDto()),
                List.of(sampleReservationDto()),
                0.0
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> bookingService.createBookingFromDto(dto));
        assertEquals("Booking must have tour.id", ex.getMessage());

        verifyNoInteractions(tourService, bookingDao, reservationDao, bookingMapper, tourDao, accommodationDao, personDao);
    }

    @Test
    void saveBooking_ShouldThrowException_WhenTourIsOverbooked() {
        BookingDto dto = new BookingDto(
                null,
                null,
                new TourDto(tour.getId(), null, null, null, 0, 0.0),
                List.of(samplePersonDto()),
                List.of(sampleReservationDto()),
                0.0
        );

        when(tourDao.find(tour.getId())).thenReturn(tour);
        when(personDao.find(1L)).thenReturn(person);
        doThrow(new IllegalStateException("Tour capacity exceeded")).when(tourService).validateCapacity(eq(tour), eq(1));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> bookingService.createBookingFromDto(dto));
        assertEquals("Tour capacity exceeded", ex.getMessage());

        verify(tourService).validateCapacity(tour, 1);
        verify(bookingDao, never()).save(any());
    }

    @Test
    void saveBooking_ShouldCalculatePrice_ValidateCapacity_AndSave() {
        BookingDto dto = new BookingDto(
                null,
                null,
                new TourDto(tour.getId(), null, null, null, 0, 0.0),
                List.of(samplePersonDto()),
                List.of(sampleReservationDto()),
                0.0
        );

        when(tourDao.find(tour.getId())).thenReturn(tour);
        when(personDao.find(1L)).thenReturn(person);
        when(accommodationDao.find(accommodation.getId())).thenReturn(accommodation);
        when(reservationDao.findIntersection(anyLong(), any(), any())).thenReturn(List.of());
        when(bookingDao.nextReservationNumber()).thenReturn(123);

        bookingService.createBookingFromDto(dto);

        verify(tourService).validateCapacity(tour, 1);
        verify(reservationDao).findIntersection(anyLong(), any(), any());

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingDao).save(bookingCaptor.capture());
        Booking saved = bookingCaptor.getValue();

        assertEquals(123, saved.getReservationNumber());
        assertEquals(800.0, saved.getTotalPrice(), 0.0001);
    }


    @Test
    void saveBooking_ShouldNotAcceptNullBooking() {
        assertThrows(NullPointerException.class, () -> bookingService.createBookingFromDto(null));
        verifyNoInteractions(tourService, bookingDao, reservationDao, bookingMapper, tourDao, accommodationDao, personDao);
    }
}
