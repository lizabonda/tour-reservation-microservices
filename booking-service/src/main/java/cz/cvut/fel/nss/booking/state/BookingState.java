package cz.cvut.fel.nss.booking.state;

import cz.cvut.fel.nss.booking.Booking;

public interface BookingState {
    void cancel(Booking booking);

    void pay(Booking booking);
}
