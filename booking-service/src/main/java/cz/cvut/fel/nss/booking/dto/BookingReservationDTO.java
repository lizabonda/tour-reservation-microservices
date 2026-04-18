package cz.cvut.fel.nss.booking.dto;

import java.time.LocalDate;

public record BookingReservationDTO(
        LocalDate startDate,
        LocalDate endDate,
        Long accommodationId
) {}
