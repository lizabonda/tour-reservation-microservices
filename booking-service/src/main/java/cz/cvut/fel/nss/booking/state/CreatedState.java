package cz.cvut.fel.nss.booking.state;

import cz.cvut.fel.nss.entity.Booking;
import cz.cvut.fel.nss.entity.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
