package cz.cvut.fel.nss.tour.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "booking-service")
public interface BookingClient {
    @PatchMapping("/api/bookings/by-tour")
    ResponseEntity<Void> cancelBookingsByTour(@RequestParam Long tourId);
}
