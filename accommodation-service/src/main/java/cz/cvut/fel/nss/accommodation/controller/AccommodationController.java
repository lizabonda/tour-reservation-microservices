package cz.cvut.fel.nss.accommodation.controller;

import cz.cvut.fel.nss.accommodation.Accommodation;
import cz.cvut.fel.nss.accommodation.dto.AccommodationDto;
import cz.cvut.fel.nss.accommodation.dto.mapper.AccommodationMapper;
import cz.cvut.fel.nss.accommodation.service.AccommodationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
@RestController
@RequestMapping("/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;
    private final AccommodationMapper accommodationMapper;

    public AccommodationController(AccommodationService accommodationService, AccommodationMapper accommodationMapper) {
        this.accommodationService = accommodationService;
        this.accommodationMapper = accommodationMapper;
    }

    @PostMapping
    ResponseEntity<AccommodationDto> createTour (@RequestBody AccommodationDto accommodationDto) {
        Accommodation created = accommodationService.createAccommodation(accommodationDto);
        AccommodationDto response = accommodationMapper.accommodationToAccommodationDto(created);
        return ResponseEntity
                .created(URI.create("/accommodations/" + created.getId()))
                .body(response);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteAccommodation(@PathVariable Long id) {
        accommodationService.deleteAccommodation(id);
        return ResponseEntity.noContent().build();
    }
}
