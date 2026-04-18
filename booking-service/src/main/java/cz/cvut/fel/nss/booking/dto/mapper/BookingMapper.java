package cz.cvut.fel.nss.booking.dto.mapper;

import cz.cvut.fel.nss.booking.Booking;
import cz.cvut.fel.nss.booking.dto.BookingDto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto bookingToBookingDto(Booking booking);
    Booking bookingDtoToBooking(BookingDto dto);
}
