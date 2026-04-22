package cz.cvut.fel.nss.booking.client;

import cz.cvut.fel.nss.booking.dto.accommodation.AccommodationPricingSummaryDto;
import cz.cvut.fel.nss.booking.dto.accommodation.ReservationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "accommodation-service")
public interface AccommodationClient {
    @PostMapping("/reservations/calculate-price")
    AccommodationPricingSummaryDto calculatePrice(@RequestBody List<ReservationDto> reservationsDto);

    @PostMapping("/reservations")
    List<ReservationDto> createReservations(@RequestBody List<ReservationDto> reservationsDto, @RequestParam("bookingId") Long bookingId);
}
