package cz.cvut.fel.nss.accommodation.facade;

import cz.cvut.fel.nss.accommodation.dao.AccommodationDao;
import cz.cvut.fel.nss.accommodation.dao.ReservationDao;
import cz.cvut.fel.nss.accommodation.entity.Accommodation;
import cz.cvut.fel.nss.accommodation.entity.Reservation;
import cz.cvut.fel.nss.exception.NotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AccommodationDaoFacade {

    private final AccommodationDao accommodationDao;
    private final ReservationDao reservationDao;

    public AccommodationDaoFacade(AccommodationDao accommodationDao, ReservationDao reservationDao) {
        this.accommodationDao = accommodationDao;
        this.reservationDao = reservationDao;
    }

    public Accommodation findAccommodationById(Long id) {
        Accommodation accommodation = accommodationDao.find(id);
        if (accommodation == null) {
            throw new NotFoundException("Accommodation not found: " + id);
        }
        return accommodation;
    }

    public Reservation findReservationById(Long id) {
        Reservation reservation = reservationDao.find(id);
        if (reservation == null) {
            throw new NotFoundException("Reservation not found: " + id);
        }
        return reservation;
    }

    public void saveAccommodation(Accommodation accommodation) {
        accommodationDao.save(accommodation);
    }

    public void updateAccommodation(Accommodation accommodation) {
        accommodationDao.update(accommodation);
    }

    public void saveReservation(Reservation reservation) {
        reservationDao.save(reservation);
    }

    public void updateReservation(Reservation reservation) {
        reservationDao.update(reservation);
    }

    public List<Reservation> findAllReservationsByAccommodationId(Long accommodationId) {
        return reservationDao.findAllByAccommodationId(accommodationId);
    }

    public List<Reservation> findAllReservationsByBookingId(Long bookingId) {
        return reservationDao.findAllByBookingId(bookingId);
    }

    public boolean hasIntersection(Long accommodationId, LocalDate startDate, LocalDate endDate, Long excludeReservationId) {
        List<Reservation> intersections = reservationDao.findIntersection(accommodationId, startDate, endDate);
        if (excludeReservationId == null) {
            return !intersections.isEmpty();
        }
        return intersections.stream().anyMatch(r -> !r.getId().equals(excludeReservationId));
    }
}
