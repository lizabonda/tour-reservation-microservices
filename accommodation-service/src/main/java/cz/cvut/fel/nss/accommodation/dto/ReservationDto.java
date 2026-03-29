package cz.cvut.fel.nss.accommodation.dto;

import java.time.LocalDate;

public record ReservationDto(Long id,
                             LocalDate startDate,
                             LocalDate endDate,
                             double reservationPrice,
                             AccommodationDto accommodation,
                             Long bookingId) {}
