package cz.cvut.fel.nss.booking.dto.booking;

import java.util.List;

public record CreateBookingDTO(
        Long tourId,
        List<BookingPersonDTO> persons,
        List<BookingReservationDTO> reservations
) {}
