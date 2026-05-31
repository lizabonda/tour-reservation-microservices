package cz.cvut.fel.nss.booking.state;

import cz.cvut.fel.nss.booking.Booking;
import cz.cvut.fel.nss.booking.BookingStatus;

public class CreatedState implements BookingState {
    @Override
    public void pay(Booking booking) {
    booking.setStatus(BookingStatus.PAID);
}

    @Override
    public void cancel(Booking booking) {
        booking.setStatus(BookingStatus.CANCELLED);
    }
}
