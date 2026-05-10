package cz.cvut.fel.nss.booking.controller;


import cz.cvut.fel.nss.entity.Booking;
import cz.cvut.fel.nss.booking.dto.BookingDto;
import cz.cvut.fel.nss.booking.dto.CreateBookingDTO;
import cz.cvut.fel.nss.booking.dto.mapper.BookingMapper;
import cz.cvut.fel.nss.booking.service.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @GetMapping("/{id}")
    ResponseEntity<BookingDto> getById(@PathVariable Long id) {
        Booking booking = bookingService.findById(id);
        return ResponseEntity.ok(bookingMapper.bookingToBookingDto(booking));
    }

    @PostMapping
    ResponseEntity<BookingDto> create(@RequestBody CreateBookingDTO request) {
        Booking created = bookingService.createBookingFromDto(request);
        BookingDto response = bookingMapper.bookingToBookingDto(created);
        return ResponseEntity
                .created(URI.create("/api/bookings/" + created.getId()))
                .body(response);
    }


    @GetMapping
    ResponseEntity<List<BookingDto>> getBookingsCreatedBetween(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<BookingDto> bookings = bookingService.getBookingsCreatedBetween(from, to);
        return ResponseEntity.ok(bookings);
    }


    @PatchMapping("/tour/cancel/{tourId}")
    ResponseEntity<Void> cancelBookingsByTourId(@PathVariable Long tourId) {
        bookingService.cancelBookingByTour(tourId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping ("/{id}")
    // when we delete accomodation
    ResponseEntity<Void> removeBookingByIdInternally(@PathVariable Long id) {
        bookingService.removeBookingByIdBySystem(id);
        return ResponseEntity.noContent().build();
    }

    //when user delete booking
    @DeleteMapping("/user/{id}")
    ResponseEntity<Void> cancelBookingByUser(@PathVariable("id") Long id) {
        bookingService.cancelBookingByUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    ResponseEntity<List<BookingDto>> getBookingsByUser(@PathVariable Long userId) {
        List<BookingDto> bookings = bookingService.findByUser(userId);
        return ResponseEntity.ok(bookings);
    }
}
