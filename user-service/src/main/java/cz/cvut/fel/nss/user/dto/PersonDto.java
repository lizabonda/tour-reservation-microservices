package cz.cvut.fel.nss.user.dto;

import java.time.LocalDate;

public record PersonDto(Long id,
                        String firstName,
                        String lastName,
                        LocalDate dateOfBirth
                        ) {}
