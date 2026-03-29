package cz.cvut.fel.nss.booking.dto;

import java.util.List;

public record BookingDto(Long id,
                         Integer reservationNumber,
                         TourDto tour,
                         List<PersonDto> persons,
                         List<ReservationDto> reservations,
                         double totalPrice){}
