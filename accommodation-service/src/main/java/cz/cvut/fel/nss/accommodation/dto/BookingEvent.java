package cz.cvut.fel.nss.accommodation.dto;

public record BookingEvent(
        Long bookingId,
        Long tourId,
        int personsCount
) {}
