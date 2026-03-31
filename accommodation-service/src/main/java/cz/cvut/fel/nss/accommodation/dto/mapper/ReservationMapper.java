package cz.cvut.fel.nss.accommodation.dto.mapper;

import cz.cvut.fel.nss.accommodation.Reservation;
import cz.cvut.fel.nss.accommodation.dto.ReservationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AccommodationMapper.class})
public interface ReservationMapper {
    Reservation reservationDtoToReservation(ReservationDto dto);
    ReservationDto reservationToReservationDto(Reservation reservation);


}
