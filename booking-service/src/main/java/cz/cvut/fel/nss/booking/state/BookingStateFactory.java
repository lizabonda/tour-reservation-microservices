package cz.cvut.fel.nss.booking.state;

import cz.cvut.fel.nss.booking.BookingStatus;

public class BookingStateFactory {
    public static BookingState getState(BookingStatus status) {
        return switch (status) {
            case CREATED -> new CreatedState();
            case PAID -> new PaidState();
            case CANCELLED -> new CancelledState();
        };
    }
}
