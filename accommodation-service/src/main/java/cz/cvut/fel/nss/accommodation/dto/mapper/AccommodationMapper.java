package cz.cvut.fel.nss.accommodation.dto.mapper;

import cz.cvut.fel.nss.accommodation.Accommodation;
import cz.cvut.fel.nss.accommodation.dto.AccommodationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccommodationMapper {
    Accommodation accommodationDtoToAccommodation(AccommodationDto dto);
    AccommodationDto  accommodationToAccommodationDto(Accommodation accommodation);
}
