package cz.cvut.fel.nss.accommodation.controller;

import cz.cvut.fel.nss.entity.Reservation;
import cz.cvut.fel.nss.accommodation.dto.AccommodationPricingSummaryDto;
import cz.cvut.fel.nss.accommodation.dto.ReservationDto;
import cz.cvut.fel.nss.accommodation.dto.mapper.ReservationMapper;
import cz.cvut.fel.nss.accommodation.service.AccommodationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final AccommodationService accommodationService;
    private final ReservationMapper reservationMapper;

    public ReservationController(AccommodationService accommodationService, ReservationMapper reservationMapper) {
        this.accommodationService = accommodationService;
        this.reservationMapper = reservationMapper;
    }


    @PostMapping("/calculate-price")
    public AccommodationPricingSummaryDto calculatePrice(@RequestBody List<ReservationDto> reservationsDto) {
        return accommodationService.calculatePrice(reservationsDto);
    }

    @PostMapping
    public List<ReservationDto> createReservations(@RequestBody List<ReservationDto> reservationsDto, @RequestParam Long bookingId) {
        List<Reservation> reservations = accommodationService.createReservations(reservationsDto, bookingId);
        return reservations.stream()
                .map(reservationMapper::reservationToReservationDto)
                .collect(Collectors.toList());
    }

//    @DeleteMapping("/booking/cancel/{bookingId}")
//    ResponseEntity<Void> cancelReservationsByBookingId(@PathVariable Long bookingId) {
//        accommodationService.cancelReservationsByBookingId(bookingId);
//        return ResponseEntity.noContent().build();
//    }
}
