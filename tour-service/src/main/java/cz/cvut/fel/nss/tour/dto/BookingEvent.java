package cz.cvut.fel.nss.tour.dto;

public record BookingEvent(
        Long bookingId,
        Long tourId,
        int personsCount
) {}
