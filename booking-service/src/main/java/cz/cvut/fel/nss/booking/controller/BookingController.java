package cz.cvut.fel.nss.booking.controller;

import cz.cvut.fel.nss.booking.entity.Booking;
import cz.cvut.fel.nss.booking.dto.booking.BookingDto;
import cz.cvut.fel.nss.booking.dto.booking.CreateBookingDTO;
import cz.cvut.fel.nss.booking.dto.mapper.BookingMapper;
import cz.cvut.fel.nss.booking.service.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller exposing booking creation, lookup and cancellation endpoints.
 */
@RestController
@RequestMapping("/api/bookings")
class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    /**
     * Returns a booking by id.
     *
     * @param id booking id
     * @return booking response
     */
    @GetMapping("/{id}")
    ResponseEntity<BookingDto> getById(@PathVariable Long id) {
        Booking booking = bookingService.findById(id);
        return ResponseEntity.ok(bookingMapper.bookingToBookingDto(booking));
    }

    /**
     * Creates a new booking.
     *
     * @param request booking creation payload
     * @return created booking response
     */
    @PostMapping
    ResponseEntity<BookingDto> create(@RequestBody CreateBookingDTO request) {
        Booking created = bookingService.createBookingFromDto(request);
        BookingDto response = bookingMapper.bookingToBookingDto(created);
        return ResponseEntity
                .created(URI.create("/api/bookings/" + created.getId()))
                .body(response);
    }

    /**
     * Returns bookings created in the given date range.
     *
     * @param from start date
     * @param to end date
     * @return matching bookings
     */
    @GetMapping
    ResponseEntity<List<BookingDto>> getBookingsCreatedBetween(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<BookingDto> bookings = bookingService.getBookingsCreatedBetween(from, to);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Cancels a booking by user request.
     *
     * @param id booking id
     * @return empty response
     */
    @DeleteMapping("/user/{id}")
    ResponseEntity<Void> cancelBookingByUser(@PathVariable("id") Long id) {
        bookingService.cancelBookingByUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns bookings connected to a user/person.
     *
     * @param userId user/person id
     * @return matching bookings
     */
    @GetMapping("/user/{userId}")
    ResponseEntity<List<BookingDto>> getBookingsByUser(@PathVariable Long userId) {
        List<BookingDto> bookings = bookingService.findByUser(userId);
        return ResponseEntity.ok(bookings);
    }
}
