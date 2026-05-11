package cz.cvut.fel.nss.booking.client;

import cz.cvut.fel.nss.booking.dto.tour.TourDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "tour-service")
public interface TourClient {
    @GetMapping("/tours/{id}")
    TourDto getTour(@PathVariable("id") Long id);

    @PutMapping("/tours/{id}/capacity")
    void updateCapacity(@PathVariable("id") Long id, @RequestParam("change") int change);
}
