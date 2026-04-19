package cz.cvut.fel.nss.booking.client;

import cz.cvut.fel.nss.booking.dto.tour.TourDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tour-service", url = "${app.tour-service.url}")
public interface TourClient {
    @GetMapping("/tours/{id}")
    TourDto getTour(@PathVariable("id") Long id);
}
