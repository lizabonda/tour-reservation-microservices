package cz.cvut.fel.nss.tour.dto.mapper;

import cz.cvut.fel.nss.tour.Tour;
import cz.cvut.fel.nss.tour.dto.TourDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TourMapper {
    Tour tourDtoToTour(TourDto dto);
    TourDto tourToTourDto(Tour tour);
}
