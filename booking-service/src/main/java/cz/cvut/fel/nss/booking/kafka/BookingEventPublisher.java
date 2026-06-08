package cz.cvut.fel.nss.booking.kafka;

import cz.cvut.fel.nss.avro.BookingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookingEventPublisher {

    private static final String TOUR_CAPACITY_TOPIC = "tour-capacity";
    private static final String BOOKING_CANCELLED_TOPIC = "booking-cancelled";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BookingEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTourCapacityChanged(Long bookingId, Long tourId, int personsCount) {
        BookingEvent event = BookingEvent.newBuilder()
                .setBookingId(bookingId)
                .setTourId(tourId)
                .setPersonsCount(personsCount)
                .build();
        kafkaTemplate.send(TOUR_CAPACITY_TOPIC, event);
    }

    public void publishBookingCancelled(Long bookingId, Long tourId, int personsCount) {
        BookingEvent event = BookingEvent.newBuilder()
                .setBookingId(bookingId)
                .setTourId(tourId)
                .setPersonsCount(personsCount)
                .build();
        kafkaTemplate.send(BOOKING_CANCELLED_TOPIC, event);
    }
}
