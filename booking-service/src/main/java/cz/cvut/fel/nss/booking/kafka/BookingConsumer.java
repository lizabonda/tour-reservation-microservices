package cz.cvut.fel.nss.booking.kafka;

import cz.cvut.fel.nss.avro.BookingEvent;
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
    public void bookingCancellationByTour(BookingEvent event) {
        bookingService.cancelBookingByTour(event.getTourId());
    }

    @KafkaListener(topics = "accommodation-cancel" )
    public void bookingCancellationByAccommodation(BookingEvent event) {
        bookingService.removeBookingByIdBySystem(event.getBookingId());
    }
}
