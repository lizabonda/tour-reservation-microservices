package cz.cvut.fel.nss.accommodation.dto.mapper;

import cz.cvut.fel.nss.projekt.dto.AccommodationDto;
import cz.cvut.fel.nss.projekt.model.Accommodation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccommodationMapper {
    Accommodation accommodationDtoToAccommodation(AccommodationDto dto);
    AccommodationDto  accommodationToAccommodationDto(Accommodation accommodation);
}
