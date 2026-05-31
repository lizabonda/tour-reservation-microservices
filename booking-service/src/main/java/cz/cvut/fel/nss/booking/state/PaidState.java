package cz.cvut.fel.nss.booking.state;

import cz.cvut.fel.nss.booking.Booking;
import cz.cvut.fel.nss.booking.BookingStatus;

public class PaidState implements BookingState {
    @Override
    public void pay(Booking booking) {
        throw new IllegalStateException("Booking is already paid");
    }

    @Override
    public void cancel(Booking booking) {
        booking.setStatus(BookingStatus.CANCELLED);
    }
}
