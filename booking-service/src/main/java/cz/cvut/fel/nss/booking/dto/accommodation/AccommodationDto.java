package cz.cvut.fel.nss.booking.dto.accommodation;

public record AccommodationDto(Long id,
                               String name,
                               String address,
                               double pricePerNight,
                               String roomType
) {}
