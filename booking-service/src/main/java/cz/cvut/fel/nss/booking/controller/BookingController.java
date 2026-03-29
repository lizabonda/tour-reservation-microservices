package cz.cvut.fel.nss.booking.controller;

import cz.cvut.fel.nss.projekt.dto.BookingDto;
import cz.cvut.fel.nss.projekt.dto.mapper.BookingMapper;
import cz.cvut.fel.nss.projekt.model.Booking;
import cz.cvut.fel.nss.projekt.service.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
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
    ResponseEntity<BookingDto> create(@RequestBody BookingDto request) {
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


    @PutMapping("/reservations/{reservationId}/accommodation/{accommodationId}")
    ResponseEntity<Void> updateReservationAccommodation(
            @PathVariable Long reservationId,
            @PathVariable Long accommodationId
    ) {
        bookingService.updateBookingAccommodation(reservationId, accommodationId);
        return ResponseEntity.noContent().build();
    }

    
    @DeleteMapping("/by-tour")
    ResponseEntity<Void> removeBookingsByTour(
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate
    ) {
        bookingService.removeBookingByTour(destination, startDate);
        return ResponseEntity.noContent().build();
    }
}
