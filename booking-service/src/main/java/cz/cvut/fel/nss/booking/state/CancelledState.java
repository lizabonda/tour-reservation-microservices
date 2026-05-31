package cz.cvut.fel.nss.booking.state;

import cz.cvut.fel.nss.booking.Booking;

public class CancelledState implements BookingState {
    @Override
    public void pay(Booking booking) {
        throw new IllegalStateException("Cancelled booking cannot be paid");
    }

    @Override
    public void cancel(Booking booking) {
        throw new IllegalStateException("Booking is already cancelled");
    }
}
