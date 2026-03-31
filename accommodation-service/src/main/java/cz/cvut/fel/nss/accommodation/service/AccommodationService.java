package cz.cvut.fel.nss.accommodation.service;

import cz.cvut.fel.nss.accommodation.Accommodation;
import cz.cvut.fel.nss.accommodation.MealPlan;
import cz.cvut.fel.nss.accommodation.Reservation;
import cz.cvut.fel.nss.accommodation.dao.AccommodationDao;
import cz.cvut.fel.nss.accommodation.dao.ReservationDao;
import cz.cvut.fel.nss.accommodation.dto.AccommodationPricingSummaryDto;
import cz.cvut.fel.nss.accommodation.dto.ReservationDto;
import cz.cvut.fel.nss.accommodation.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccommodationService {

    private static final double ALL_INCLUSIVE_PERCENT = 0.15;

    private final AccommodationDao accommodationDao;
    private final ReservationDao reservationDao;

    public AccommodationService(AccommodationDao accommodationDao, ReservationDao reservationDao) {
        this.accommodationDao = accommodationDao;
        this.reservationDao = reservationDao;
    }

    public AccommodationPricingSummaryDto calculatePrice(List<ReservationDto> reservationsDto) {
        if (reservationsDto == null || reservationsDto.isEmpty()) {
            throw new IllegalArgumentException("Reservations must not be empty");
        }

        double accommodationPrice = 0.0;
        double allInclusiveCharge = 0.0;

        for (ReservationDto reservationDto : reservationsDto) {
            if (reservationDto == null) {
                throw new IllegalArgumentException("Reservation must not be null");
            }
            if (reservationDto.startDate() == null || reservationDto.endDate() == null) {
                throw new IllegalArgumentException("Reservation must have startDate and endDate");
            }
            if (reservationDto.accommodation() == null || reservationDto.accommodation().id() == null) {
                throw new IllegalArgumentException("Reservation must have accommodation.id");
            }

            Accommodation accommodation = accommodationDao.find(reservationDto.accommodation().id());
            if (accommodation == null) {
                throw new NotFoundException("Accommodation not found: " + reservationDto.accommodation().id());
            }

            Reservation reservation = new Reservation();
            reservation.setStartDate(reservationDto.startDate());
            reservation.setEndDate(reservationDto.endDate());
            reservation.setAccommodation(accommodation);
            reservation.calculateReservationPrice();

            accommodationPrice += reservation.getReservationPrice();

            if (accommodation.getMealPlan() == MealPlan.ALL_INCLUSIVE) {
                allInclusiveCharge += reservation.getReservationPrice() * ALL_INCLUSIVE_PERCENT;
            }
        }

        return new AccommodationPricingSummaryDto(accommodationPrice, allInclusiveCharge);
    }

    public List<Reservation> createReservations(List<ReservationDto> reservationsDto, Long bookingId) {
        final List<Reservation> reservations = new ArrayList<>(reservationsDto.size());
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking id must not be null");
        }
        for (ReservationDto reservationDto : reservationsDto) {
            if (reservationDto == null) {
                throw new IllegalArgumentException("Reservation must not be null");
            }
            if (reservationDto.id() != null) {
                throw new IllegalArgumentException("Reservation id must be null on create");
            }
            if (reservationDto.bookingId() != null) {
                throw new IllegalArgumentException("Reservation bookingId must be null on create");
            }
            if (reservationDto.startDate() == null || reservationDto.endDate() == null) {
                throw new IllegalArgumentException("Reservation must have startDate and endDate");
            }
            if (reservationDto.accommodation() == null || reservationDto.accommodation().id() == null) {
                throw new IllegalArgumentException("Reservation must have accommodation.id");
            }

            if (reservationDto.reservationPrice() != 0.0) {
                throw new IllegalArgumentException("Reservation price must not be provided on create");
            }
            final Accommodation accommodation = accommodationDao.find(reservationDto.accommodation().id());
            if (accommodation == null) {
                throw new NotFoundException("Accommodation not found: " + reservationDto.accommodation().id());
            }

            final LocalDate startDate = reservationDto.startDate();
            final LocalDate endDate = reservationDto.endDate();
            final Reservation reservation = new Reservation();
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            reservation.setAccommodation(accommodation);
            reservation.setBookingId(bookingId);
            reservation.calculateReservationPrice();
            validationForReservation(reservation);
            reservationDao.save(reservation);
            reservations.add(reservation);
        }
        return reservations;
    }

    private void validationForReservation(Reservation r) {
        if (r.getAccommodation() == null) {
            throw new IllegalArgumentException("Reservation must have accommodation");
        }
        if (r.getStartDate() == null || r.getEndDate() == null) {
            throw new IllegalArgumentException("Reservation must have startDate and endDate");
        }
        if (!r.getEndDate().isAfter(r.getStartDate())) {
            throw new IllegalArgumentException("Reservation endDate must be after startDate");
        }

        List<Reservation> intersection = reservationDao.findIntersection(
                r.getAccommodation().getId(),
                r.getStartDate(),
                r.getEndDate()
        );
        if (!intersection.isEmpty()) {
            throw new IllegalStateException("Accommodation is not available for given dates");
        }
    }

    public void updateBookingAccommodation(Long reservationId, Long newaccommodationId) {

        Reservation r = reservationDao.find(reservationId);
        if (r == null) {
            throw new NotFoundException("Reservation not found: " + reservationId);
        }

        Accommodation newAccommodation= accommodationDao.find(newaccommodationId);
        if (newAccommodation == null) {
            throw new NotFoundException("Accommodation not found: " + newaccommodationId);
        }

        List<Reservation> intersection= reservationDao.findIntersection(newaccommodationId, r.getStartDate(),r.getEndDate());
        Reservation toDelete=null;
        for (Reservation reservation:intersection) {
            if(reservation.getId().equals(reservationId)) {
                toDelete=reservation;
                break;
            }
        }
        intersection.remove(toDelete);
        if (!intersection.isEmpty()) {
            throw new IllegalStateException("Accommodation is not available for given dates");
        }

        r.setAccommodation(newAccommodation);
        r.calculateReservationPrice();
        reservationDao.update(r);
    }


}
