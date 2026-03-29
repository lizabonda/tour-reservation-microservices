package cz.cvut.fel.nss.tour.service;

import cz.cvut.fel.nss.projekt.dao.BookingDao;
import cz.cvut.fel.nss.projekt.model.Tour;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class TourService {

    private final BookingDao bookingDao;

    public TourService(BookingDao bookingDao) {
        this.bookingDao = bookingDao;
    }

    @Transactional(readOnly = true)
    public void validateCapacity(Tour tour, int requestedSize) {
        Objects.requireNonNull(tour);
        int capacity = tour.getCapacity();
        int occupied= bookingDao.countPersonsByTour(tour.getId());

        if(requestedSize+occupied>capacity) {
            throw new IllegalStateException(
                "Tour capacity exceeded: capacity=" + capacity
        );
        }
    }

}
