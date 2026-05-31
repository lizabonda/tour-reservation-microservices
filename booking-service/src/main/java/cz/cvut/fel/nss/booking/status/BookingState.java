package cz.cvut.fel.nss.booking.status;

import cz.cvut.fel.nss.booking.entity.Booking;

public interface BookingState {
    void cancel(Booking booking);

    void pay(Booking booking);
}
