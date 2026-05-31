package cz.cvut.fel.nss.booking.dto.booking;

import cz.cvut.fel.nss.booking.entity.BookingStatus;
import java.util.List;

public record BookingDto(Long id,
                         Integer bookingNumber,
                         Long tourId,
                         List<Long> personIds,
                         List<Long> reservationIds,
                         double totalPrice,
                         BookingStatus status){

}
