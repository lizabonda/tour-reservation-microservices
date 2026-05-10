package cz.cvut.fel.nss.accommodation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "booking-service",path = "/api/bookings")
public interface BookingClient {

    @DeleteMapping ("/{id}")
    ResponseEntity<Void> removeBookingByIdInternally(@PathVariable Long id);
}