package cz.cvut.fel.nss.tour.controller;

import cz.cvut.fel.nss.entity.Tour;
import cz.cvut.fel.nss.tour.dto.TourDto;
import cz.cvut.fel.nss.tour.dto.mapper.TourMapper;
import cz.cvut.fel.nss.tour.service.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/date")
    public List<TourDto> getToursByDate(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        List<Tour> tours= tourService.findByDate(startDate,endDate);
        return tours.stream().map(tour -> tourMapper.tourToTourDto(tour)).toList();
    }

    @PostMapping
    ResponseEntity <TourDto> createTour (@RequestBody TourDto tourDto) {
        Tour created = tourService.createTour(tourDto);
        TourDto response = tourMapper.tourToTourDto(created);
        return ResponseEntity
                .created(URI.create("/tours/" + created.getId()))
                .body(response);
    }

    @PatchMapping("/{id}")
    ResponseEntity<Void> cancelTour(@PathVariable Long id) {
        tourService.cancelTour(id);
        return ResponseEntity.noContent().build();
    }
}
