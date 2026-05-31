package cz.cvut.fel.nss.booking.status;

import cz.cvut.fel.nss.booking.entity.Booking;
import cz.cvut.fel.nss.booking.entity.BookingStatus;

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
