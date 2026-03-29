package cz.cvut.fel.nss.tour.dto.mapper;

import cz.cvut.fel.nss.projekt.dto.TourDto;
import cz.cvut.fel.nss.projekt.model.Tour;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AccommodationMapper.class})
public interface TourMapper {
    Tour tourDtoToTour(TourDto dto);
    TourDto tourToTourDto(Tour tour);
}
