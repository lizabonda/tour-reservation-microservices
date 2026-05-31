package cz.cvut.fel.nss.tour.dto.mapper;

import cz.cvut.fel.nss.tour.entity.Tour;
import cz.cvut.fel.nss.tour.dto.TourDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TourMapper {
    @Mapping(target = "id", ignore = true)
    Tour tourDtoToTour(TourDto dto);

    TourDto tourToTourDto(Tour tour);
}
