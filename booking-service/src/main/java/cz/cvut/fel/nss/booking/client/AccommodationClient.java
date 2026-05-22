package cz.cvut.fel.nss.booking.client;

import cz.cvut.fel.nss.booking.dto.accommodation.AccommodationPricingSummaryDto;
import cz.cvut.fel.nss.booking.dto.accommodation.ReservationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "accommodation-service")
public interface AccommodationClient {
    @PostMapping("/reservations/calculate-price")
    AccommodationPricingSummaryDto calculatePrice(@RequestBody List<ReservationDto> reservationsDto);

    @PostMapping("/reservations")
    List<ReservationDto> createReservations(@RequestBody List<ReservationDto> reservationsDto, @RequestParam("bookingId") Long bookingId);

//    @DeleteMapping ("/reservations/booking/cancel/{bookingId}")
//    ResponseEntity<Void> cancelReservationsByBookingId(@PathVariable Long bookingId);

}
