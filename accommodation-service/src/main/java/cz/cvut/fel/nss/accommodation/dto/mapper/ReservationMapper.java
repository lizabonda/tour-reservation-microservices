package cz.cvut.fel.nss.accommodation.dto.mapper;

import cz.cvut.fel.nss.accommodation.entity.Reservation;
import cz.cvut.fel.nss.accommodation.dto.ReservationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AccommodationMapper.class})
public interface ReservationMapper {
    @Mapping(source = "accommodation.id", target = "accommodationId")
    ReservationDto reservationToReservationDto(Reservation reservation);

    @Mapping(source = "accommodationId", target = "accommodation.id")
    Reservation reservationDtoToReservation(ReservationDto dto);


}
