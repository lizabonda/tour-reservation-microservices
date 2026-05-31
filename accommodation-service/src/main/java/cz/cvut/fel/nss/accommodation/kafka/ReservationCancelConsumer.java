package cz.cvut.fel.nss.accommodation.kafka;

import cz.cvut.fel.nss.avro.BookingEvent;
import cz.cvut.fel.nss.accommodation.service.AccommodationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReservationCancelConsumer {
    private final AccommodationService accommodationService;

    public ReservationCancelConsumer(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    @KafkaListener(topics = "booking-cancelled")
    public void processReservationCancellation(BookingEvent event){
        accommodationService.cancelReservationsByBookingId(event.getBookingId());

    }
}
