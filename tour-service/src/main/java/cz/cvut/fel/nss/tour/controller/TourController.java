package cz.cvut.fel.nss.tour.controller;

import cz.cvut.fel.nss.tour.Tour;
import cz.cvut.fel.nss.tour.dto.TourDto;
import cz.cvut.fel.nss.tour.dto.mapper.TourMapper;
import cz.cvut.fel.nss.tour.service.TourService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tours")
public class TourController {

    private final TourService tourService;
    private final TourMapper tourMapper;

    public TourController(TourService tourService, TourMapper tourMapper) {
        this.tourService = tourService;
        this.tourMapper = tourMapper;
    }

    @GetMapping("/{id}")
    public TourDto getTour(@PathVariable Long id) {
        Tour tour = tourService.findById(id);
        if (tour == null) {
            return null;
        }
        return tourMapper.tourToTourDto(tour);
    }
}
