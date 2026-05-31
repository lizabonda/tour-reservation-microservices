package cz.cvut.fel.nss.booking.status;

import cz.cvut.fel.nss.booking.entity.BookingStatus;

public class BookingStateFactory {
    public static BookingState getState(BookingStatus status) {
        return switch (status) {
            case CREATED -> new CreatedState();
            case PAID -> new PaidState();
            case CANCELLED -> new CancelledState();
        };
    }
}
