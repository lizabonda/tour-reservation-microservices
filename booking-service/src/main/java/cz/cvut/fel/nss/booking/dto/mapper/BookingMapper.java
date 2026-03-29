package cz.cvut.fel.nss.booking.dto.mapper;

import cz.cvut.fel.nss.booking.dto.BookingDto;
import cz.cvut.fel.nss.projekt.dto.BookingDto;
import cz.cvut.fel.nss.projekt.model.Booking;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {
                TourMapper.class,
                PersonMapper.class,
                ReservationMapper.class
        }
)
public interface BookingMapper {
    Booking bookingDtoToBooking(BookingDto dto);
    BookingDto  bookingToBookingDto(Booking booking);
}
