package cz.cvut.fel.nss.booking.status;

import cz.cvut.fel.nss.booking.entity.Booking;
import cz.cvut.fel.nss.booking.entity.BookingStatus;

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
