package cz.cvut.fel.nss.tour.service;


import cz.cvut.fel.nss.tour.Tour;
import cz.cvut.fel.nss.tour.dao.TourDao;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TourService {

    private final TourDao tourDao;

    public TourService(TourDao tourDao) {
        this.tourDao = tourDao;
    }

    public Tour findById(Long id) {
        return tourDao.find(id);
    }

    public List<Tour> findByDate(LocalDate startDate, LocalDate endDate) {
        return tourDao.findByDate(startDate,endDate);
    }
}

