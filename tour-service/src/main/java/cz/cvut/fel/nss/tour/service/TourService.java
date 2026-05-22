package cz.cvut.fel.nss.tour.service;


import cz.cvut.fel.nss.entity.Tour;
import cz.cvut.fel.nss.entity.TourStatus;
//import cz.cvut.fel.nss.tour.client.BookingClient;
import cz.cvut.fel.nss.tour.dao.TourDao;
import cz.cvut.fel.nss.tour.dto.TourDto;
import cz.cvut.fel.nss.tour.dto.mapper.TourMapper;
import cz.cvut.fel.nss.tour.exception.NotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TourService {

    private final TourDao tourDao;
    private final TourMapper tourMapper;
//    private final BookingClient bookingClient;
    private final KafkaTemplate<String, Long> kafkaTemplate;

    public TourService(TourDao tourDao, TourMapper tourMapper,  KafkaTemplate<String, Long> kafkaTemplate) {
        this.tourDao = tourDao;
        this.tourMapper = tourMapper;
//        this.bookingClient = bookingClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Cacheable(value = "tours", key = "#id")
    public Tour findById(Long id) {
        return tourDao.find(id);
    }

    @Cacheable(value = "toursByDate", key = "#startDate + ':' + #endDate")
    public List<Tour> findByDate(LocalDate startDate, LocalDate endDate) {
        return tourDao.findByDate(startDate,endDate);
    }

    @CacheEvict(value = "toursByDate", allEntries = true)
    public Tour createTour(TourDto tourDto) {
        Tour tour = tourMapper.tourDtoToTour(tourDto);
        if (tour.getStatus() == null) {
            tour.setStatus(TourStatus.ACTIVE);
        }
        tourDao.save(tour);
        return tour;
    }
    @CacheEvict(value = {"tours", "toursByDate"}, allEntries = true)
    public void cancelTour (Long tourId) {
        Tour tour = tourDao.find(tourId);
        if (tour == null) {
            throw new NotFoundException("Tour not found: " + tourId);
        }
        tour.setStatus(TourStatus.CANCELLED);
        tourDao.update(tour);
        kafkaTemplate.send("tour-cancelled", tourId);
//        bookingClient.cancelBookingsByTourId(tourId);

    }
    @CacheEvict(value = {"tours", "toursByDate"}, allEntries = true)
    public void updateCapacity(Long tourId, int change) {
        Tour tour = tourDao.find(tourId);
        if (tour == null) {
            throw new NotFoundException("Tour not found: " + tourId);
        }
        int newCapacity = tour.getCapacity() + change;
        if (newCapacity < 0) {
            throw new IllegalStateException("Tour capacity cannot be negative");
        }
        tour.setCapacity(newCapacity);
        tourDao.update(tour);
    }
}

