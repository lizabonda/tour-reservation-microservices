package cz.cvut.fel.nss.booking.kafka;

import cz.cvut.fel.nss.booking.service.BookingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AccommodationDeleteConsumer {
    private final BookingService bookingService;

    public AccommodationDeleteConsumer(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @KafkaListener(topics = "booking-cancel" )
    public void processBookingCancellation(Long bookingId) {
        bookingService.removeBookingByIdBySystem(bookingId);
    }
}
