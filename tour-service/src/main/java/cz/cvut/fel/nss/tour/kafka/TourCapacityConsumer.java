package cz.cvut.fel.nss.tour.kafka;

import cz.cvut.fel.nss.avro.BookingEvent;
import cz.cvut.fel.nss.tour.service.TourService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TourCapacityConsumer {
    private final TourService tourService;

    public TourCapacityConsumer(TourService tourService) {
        this.tourService = tourService;
    }
    @KafkaListener(topics = "tour-capacity")
    public void processTourCapacityUpdate(BookingEvent event) {
        tourService.updateCapacity(event.getTourId(), event.getPersonsCount());
    }
}
