package cz.cvut.fel.nss.accommodation.kafka;

import cz.cvut.fel.nss.avro.BookingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AccommodationEventPublisher {

    private static final String ACCOMMODATION_CANCEL_TOPIC = "accommodation-cancel";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AccommodationEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishAccommodationCancelled(Long bookingId) {
        if (bookingId != null) {
            kafkaTemplate.send(ACCOMMODATION_CANCEL_TOPIC, new BookingEvent(bookingId, 0L, 0));
        }
    }
}
