package cz.cvut.fel.nss.booking.dto.user;

import java.time.LocalDate;

public record PersonDto(Long id,
                        String firstName,
                        String lastName,
                        LocalDate dateOfBirth
) {}
