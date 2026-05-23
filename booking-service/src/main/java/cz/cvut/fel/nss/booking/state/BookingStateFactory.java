package cz.cvut.fel.nss.booking.state;

import cz.cvut.fel.nss.entity.BookingStatus;

import java.util.EnumMap;
import java.util.Map;

public class BookingStateFactory {
    public static BookingState getState(BookingStatus status) {
        return switch (status) {
            case CREATED -> new CreatedState();
            case PAID -> new PaidState();
            case CANCELLED -> new CancelledState();
        };
    }
}
