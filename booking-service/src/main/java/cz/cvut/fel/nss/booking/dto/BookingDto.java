package cz.cvut.fel.nss.booking.dto;

import java.util.List;

public record BookingDto(Long id,
                         Integer bookingNumber,
                         Long tourId,
                         List<Long> personIds,
                         List<Long> reservationIds,
                         double totalPrice){

}
