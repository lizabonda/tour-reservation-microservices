package cz.cvut.fel.nss.booking.dto;

import java.time.LocalDate;

public record BookingPersonDTO(
        Long id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth
) {}
