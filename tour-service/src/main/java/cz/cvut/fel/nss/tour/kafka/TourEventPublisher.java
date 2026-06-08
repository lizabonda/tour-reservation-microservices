package cz.cvut.fel.nss.tour.kafka;

import cz.cvut.fel.nss.avro.BookingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TourEventPublisher {

    private static final String TOUR_CANCELLED_TOPIC = "tour-cancelled";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TourEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTourCancelled(Long tourId) {
        kafkaTemplate.send(TOUR_CANCELLED_TOPIC, new BookingEvent(0L, tourId, 0));
    }
}
