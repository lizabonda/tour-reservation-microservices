package cz.cvut.fel.nss.accommodation.dto.mapper;

import cz.cvut.fel.nss.projekt.dto.ReservationDto;
import cz.cvut.fel.nss.projekt.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AccommodationMapper.class})
public interface ReservationMapper {
    Reservation reservationDtoToReservation(ReservationDto dto);

    @Mapping(target = "bookingId",  source = "booking.id")
    ReservationDto reservationToReservationDto(Reservation reservation);


}
