package cz.cvut.fel.nss.tour.dto;
import cz.cvut.fel.nss.entity.TourStatus;
import java.time.LocalDate;

public record TourDto(Long id,
                      String title,
                      String description,
                      String destination,
                      LocalDate startDate,
                      LocalDate endDate,
                      int capacity,
                      double price,
                      TourStatus status
                      ) {}
