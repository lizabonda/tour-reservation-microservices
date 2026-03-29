package cz.cvut.fel.nss.accommodation.dto;


public record AccommodationDto(Long id,
                               String name,
                               String address,
                               double pricePerNight,
                               String roomType
) {}
