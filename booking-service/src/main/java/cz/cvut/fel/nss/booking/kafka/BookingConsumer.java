package cz.cvut.fel.nss.booking.kafka;

import cz.cvut.fel.nss.booking.service.BookingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingConsumer {
    private final BookingService bookingService;

    public BookingConsumer(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @KafkaListener(topics = "tour-cancelled" )
    public void bookingCancellationByTour(Long tourId) {
        bookingService.cancelBookingByTour(tourId);
    }

    @KafkaListener(topics = "accommodation-cancel" )
    public void bookingCancellationByAccommodation(Long bookingId) {
        bookingService.removeBookingByIdBySystem(bookingId);
    }
}
