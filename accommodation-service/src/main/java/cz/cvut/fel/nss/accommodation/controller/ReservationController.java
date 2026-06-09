package cz.cvut.fel.nss.accommodation.controller;

import cz.cvut.fel.nss.accommodation.entity.Reservation;
import cz.cvut.fel.nss.accommodation.dto.AccommodationPricingSummaryDto;
import cz.cvut.fel.nss.accommodation.dto.ReservationDto;
import cz.cvut.fel.nss.accommodation.dto.mapper.ReservationMapper;
import cz.cvut.fel.nss.accommodation.service.AccommodationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for reservation price calculation and reservation creation.
 */
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final AccommodationService accommodationService;
    private final ReservationMapper reservationMapper;

    public ReservationController(AccommodationService accommodationService, ReservationMapper reservationMapper) {
        this.accommodationService = accommodationService;
        this.reservationMapper = reservationMapper;
    }


    /**
     * Calculates accommodation price for reservation requests.
     *
     * @param reservationsDto reservation requests
     * @return calculated price summary
     */
    @PostMapping("/calculate-price")
    public AccommodationPricingSummaryDto calculatePrice(@RequestBody List<ReservationDto> reservationsDto) {
        return accommodationService.calculatePrice(reservationsDto);
    }

    /**
     * Creates reservations for an existing booking.
     *
     * @param reservationsDto reservation requests
     * @param bookingId owner booking id
     * @return created reservations as DTOs
     */
    @PostMapping
    public List<ReservationDto> createReservations(@RequestBody List<ReservationDto> reservationsDto, @RequestParam Long bookingId) {
        List<Reservation> reservations = accommodationService.createReservations(reservationsDto, bookingId);
        return reservations.stream()
                .map(reservationMapper::reservationToReservationDto)
                .collect(Collectors.toList());
    }
}
