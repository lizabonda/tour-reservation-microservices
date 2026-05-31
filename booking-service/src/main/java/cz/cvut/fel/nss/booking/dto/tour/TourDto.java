package cz.cvut.fel.nss.booking.dto.tour;


import cz.cvut.fel.nss.booking.entity.TourStatus;

import java.time.LocalDate;

public record TourDto(Long id,
                      String destination,
                      LocalDate startDate,
                      LocalDate endDate,
                      int capacity,
                      double price,
                      TourStatus status
) {}
