package cz.cvut.fel.nss.tour.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "booking-service")
public interface BookingClient {
    @PatchMapping("api/bookings/tour/cancel/{tourId}")
    ResponseEntity<Void> cancelBookingsByTourId(@PathVariable Long tourId);
}
