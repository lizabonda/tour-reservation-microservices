package cz.cvut.fel.nss.booking.dto.accommodation;

import java.time.LocalDate;

public record ReservationDto(Long id,
                             LocalDate startDate,
                             LocalDate endDate,
                             double reservationPrice,
                             AccommodationDto accommodation,
                             Long bookingId) {}
