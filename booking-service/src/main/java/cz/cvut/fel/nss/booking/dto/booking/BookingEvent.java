package cz.cvut.fel.nss.booking.dto.booking;

public record BookingEvent(
        Long bookingId,
        Long tourId,
        int personsCount
) {}
