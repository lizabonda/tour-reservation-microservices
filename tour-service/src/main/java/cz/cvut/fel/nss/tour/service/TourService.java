package cz.cvut.fel.nss.tour.service;


import cz.cvut.fel.nss.entity.Tour;
import cz.cvut.fel.nss.entity.TourStatus;
import cz.cvut.fel.nss.tour.client.BookingClient;
import cz.cvut.fel.nss.tour.dao.TourDao;
import cz.cvut.fel.nss.tour.dto.TourDto;
import cz.cvut.fel.nss.tour.dto.mapper.TourMapper;
import cz.cvut.fel.nss.tour.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TourService {

    private final TourDao tourDao;
    private final TourMapper tourMapper;
    private final BookingClient bookingClient;

    public TourService(TourDao tourDao, TourMapper tourMapper, BookingClient bookingClient) {
        this.tourDao = tourDao;
        this.tourMapper = tourMapper;
        this.bookingClient = bookingClient;
    }

    public Tour findById(Long id) {
        return tourDao.find(id);
    }

    public List<Tour> findByDate(LocalDate startDate, LocalDate endDate) {
        return tourDao.findByDate(startDate,endDate);
    }

    public Tour createTour(TourDto tourDto) {
        Tour tour = tourMapper.tourDtoToTour(tourDto);
        tourDao.save(tour);
        return tour;
    }

    public void cancelTour (Long tourId) {
        Tour tour = tourDao.find(tourId);
        if (tour == null) {
            throw new NotFoundException("Tour not found: " + tourId);
        }
        tour.setStatus(TourStatus.CANCELLED);
        tourDao.update(tour);
        bookingClient.cancelBookingsByTourId(tourId);

    }
}

