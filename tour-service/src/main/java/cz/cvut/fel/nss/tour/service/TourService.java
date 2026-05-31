package cz.cvut.fel.nss.tour.service;


import cz.cvut.fel.nss.avro.BookingEvent;
import cz.cvut.fel.nss.tour.Tour;
import cz.cvut.fel.nss.tour.TourStatus;
//import cz.cvut.fel.nss.tour.client.BookingClient;
import cz.cvut.fel.nss.tour.dao.TourDao;
import cz.cvut.fel.nss.tour.dto.TourDto;
import cz.cvut.fel.nss.tour.dto.mapper.TourMapper;
import cz.cvut.fel.nss.tour.exception.NotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TourService(TourDao tourDao, TourMapper tourMapper,  KafkaTemplate<String, Object> kafkaTemplate) {
        this.tourDao = tourDao;
        this.tourMapper = tourMapper;
//        this.bookingClient = bookingClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Cacheable(value = "tours", key = "#id")
    public Tour findById(Long id) {
        System.out.println("Loading tour from DB, id = " + id);
        return tourDao.find(id);
    }

    @Cacheable(value = "toursByDate", key = "#startDate + ':' + #endDate")
    public List<Tour> findByDate(LocalDate startDate, LocalDate endDate) {
        System.out.println("Loading tours from DB, startDate = " + startDate + ", endDate = " + endDate);
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Invalid date range");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        return tourDao.findByDate(startDate,endDate);
    }

    @CacheEvict(value = "toursByDate", allEntries = true)
    public Tour createTour(TourDto tourDto) {
        if (tourDto == null) {
            throw new IllegalArgumentException("TourDto must not be null");
        }
        if (tourDto.title() == null || tourDto.title().isBlank()) {
            throw new IllegalArgumentException("Title must not be null or blank");
        }
        if (tourDto.destination() == null || tourDto.destination().isBlank()) {
            throw new IllegalArgumentException("Destination must not be null or blank");
        }
        if (tourDto.startDate() == null) {
            throw new IllegalArgumentException("StartDate must not be null");
        }
        if (tourDto.endDate() == null) {
            throw new IllegalArgumentException("EndDate must not be null");
        }
        if (tourDto.startDate().isAfter(tourDto.endDate())) {
            throw new IllegalArgumentException("StartDate cannot be after EndDate");
        }
        if (tourDto.capacity() < 0) {
            throw new IllegalArgumentException("Capacity must be non-negative");
        }
        if (tourDto.price() < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }

        Tour tour = tourMapper.tourDtoToTour(tourDto);
        tour.setStatus(TourStatus.ACTIVE);
        tourDao.save(tour);
        return tour;
    }
    @Caching(evict = {
            @CacheEvict(value = "tours", key = "#tourId"),
            @CacheEvict(value = "toursByDate", allEntries = true)
    })
    public void cancelTour (Long tourId) {
        Tour tour = tourDao.find(tourId);
        if (tour == null) {
            throw new NotFoundException("Tour not found: " + tourId);
        }
        tour.setStatus(TourStatus.CANCELLED);
        tourDao.update(tour);
        kafkaTemplate.send("tour-cancelled", new BookingEvent(0L, tourId, 0));
//        bookingClient.cancelBookingsByTourId(tourId);

    }
    @Caching(evict = {
            @CacheEvict(value = "tours", key = "#tourId"),
            @CacheEvict(value = "toursByDate", allEntries = true)
    })
    public void updateCapacity(Long tourId, int change) {
        if (tourId == null) {
            return;
        }
        if (change == 0) {
            return;
        }
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

