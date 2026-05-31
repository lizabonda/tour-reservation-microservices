package cz.cvut.fel.nss.accommodation.dto.mapper;

import cz.cvut.fel.nss.accommodation.Accommodation;
import cz.cvut.fel.nss.accommodation.dto.AccommodationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccommodationMapper {
    @Mapping(target = "id", ignore = true)
    Accommodation accommodationDtoToAccommodation(AccommodationDto dto);

    AccommodationDto  accommodationToAccommodationDto(Accommodation accommodation);
}
