package cz.cvut.fel.nss.accommodation.service;

import cz.cvut.fel.nss.avro.BookingEvent;
import cz.cvut.fel.nss.accommodation.entity.Accommodation;
import cz.cvut.fel.nss.accommodation.entity.MealPlan;
import cz.cvut.fel.nss.accommodation.entity.Reservation;
import cz.cvut.fel.nss.accommodation.dao.AccommodationDao;
import cz.cvut.fel.nss.accommodation.dao.ReservationDao;
import cz.cvut.fel.nss.accommodation.dto.AccommodationDto;
import cz.cvut.fel.nss.accommodation.dto.AccommodationPricingSummaryDto;
import cz.cvut.fel.nss.accommodation.dto.ReservationDto;
import cz.cvut.fel.nss.accommodation.dto.mapper.AccommodationMapper;
import cz.cvut.fel.nss.exception.NotFoundException;
import cz.cvut.fel.nss.accommodation.entity.ReservationStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AccommodationService {

    private static final double ALL_INCLUSIVE_PERCENT = 0.15;

    private final AccommodationDao accommodationDao;
    private final ReservationDao reservationDao;
    private final AccommodationMapper accommodationMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AccommodationService(AccommodationDao accommodationDao, ReservationDao reservationDao, AccommodationMapper accommodationMapper,  KafkaTemplate<String, Object> kafkaTemplate) {
        this.accommodationDao = accommodationDao;
        this.reservationDao = reservationDao;
        this.accommodationMapper = accommodationMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public AccommodationPricingSummaryDto calculatePrice(List<ReservationDto> reservationsDto) {
        if (reservationsDto == null || reservationsDto.isEmpty()) {
            throw new IllegalArgumentException("Reservations must not be empty");
        }

        double accommodationPrice = 0.0;

        for (ReservationDto reservationDto : reservationsDto) {
            if (reservationDto == null) {
                throw new IllegalArgumentException("Reservation must not be null");
            }
            if (reservationDto.startDate() == null || reservationDto.endDate() == null) {
                throw new IllegalArgumentException("Reservation must have startDate and endDate");
            }
            if (reservationDto.accommodationId() == null) {
                throw new IllegalArgumentException("Reservation must have accommodation.id");
            }

            Accommodation accommodation = accommodationDao.find(reservationDto.accommodationId());
            if (accommodation == null) {
                throw new NotFoundException("Accommodation not found: " + reservationDto.accommodationId());
            }

            Reservation reservation = new Reservation();
            reservation.setStartDate(reservationDto.startDate());
            reservation.setEndDate(reservationDto.endDate());
            reservation.setAccommodation(accommodation);
            reservation.calculateReservationPrice();

            double reservationPrice = reservation.getReservationPrice();

            if (accommodation.getMealPlan() == MealPlan.ALL_INCLUSIVE) {
                reservationPrice += reservation.getReservationPrice() * ALL_INCLUSIVE_PERCENT;
            }

            accommodationPrice += reservationPrice;
        }

        return new AccommodationPricingSummaryDto(accommodationPrice);
    }

    public List<Reservation> createReservations(List<ReservationDto> reservationsDto, Long bookingId) {
        if (reservationsDto == null || reservationsDto.isEmpty()) {
            throw new IllegalArgumentException("Reservations must not be empty");
        }
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
            if (reservationDto.accommodationId() == null) {
                throw new IllegalArgumentException("Reservation must have accommodation.id");
            }

            if (reservationDto.reservationPrice() != 0.0) {
                throw new IllegalArgumentException("Reservation price must not be provided on create");
            }
            final Accommodation accommodation = accommodationDao.find(reservationDto.accommodationId());
            if (accommodation == null) {
                throw new NotFoundException("Accommodation not found: " + reservationDto.accommodationId());
            }

            final LocalDate startDate = reservationDto.startDate();
            final LocalDate endDate = reservationDto.endDate();
            final Reservation reservation = new Reservation();
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            reservation.setAccommodation(accommodation);
            reservation.setBookingId(bookingId);
            if (reservationDto.numberOfPersons() <= 0) {
                throw new IllegalArgumentException("Number of persons must be positive");
            }
            int persons = reservationDto.numberOfPersons();
            reservation.setNumberOfPersons(persons);
            reservation.calculateReservationPrice();
            validationForReservation(reservation);
            
            if (persons > accommodation.getCapacity()) {
                throw new IllegalStateException("Too many persons for this accommodation");
            }

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
        if (newaccommodationId == null) {
            throw new IllegalArgumentException("New accommodation id must not be null");
        }

        Reservation r = reservationDao.find(reservationId);
        if (r == null) {
            throw new NotFoundException("Reservation not found: " + reservationId);
        }

        Accommodation newAccommodation = accommodationDao.find(newaccommodationId);
        if (newAccommodation == null) {
            throw new NotFoundException("Accommodation not found: " + newaccommodationId);
        }

        List<Reservation> intersection = reservationDao.findIntersection(newaccommodationId, r.getStartDate(), r.getEndDate());
        Reservation toDelete = null;
        for (Reservation reservation : intersection) {
            if (reservation.getId().equals(reservationId)) {
                toDelete = reservation;
                break;
            }
        }
        intersection.remove(toDelete);
        if (!intersection.isEmpty()) {
            throw new IllegalStateException("Accommodation is not available for given dates");
        }

        if (r.getNumberOfPersons() <= 0) {
            throw new IllegalStateException("Reservation number of persons must be positive");
        }
        int persons = r.getNumberOfPersons();
        if (persons > newAccommodation.getCapacity()) {
            throw new IllegalStateException("Too many persons for new accommodation");
        }

        r.setAccommodation(newAccommodation);
        r.calculateReservationPrice();
        reservationDao.update(r);
    }

    public Accommodation createAccommodation(AccommodationDto accommodationDto) {
        Accommodation accommodation = accommodationMapper.accommodationDtoToAccommodation(accommodationDto);
        accommodationDao.save(accommodation);
        return accommodation;
    }

    public void deleteAccommodation(Long id) {
        Accommodation accommodation = accommodationDao.find(id);
        if (accommodation == null) {
            throw new NotFoundException("Accommodation not found: " + id);
        }
        accommodation.setDeleted(true);
        accommodationDao.update(accommodation);

        List<Reservation> reservations = reservationDao.findAllByAccommodationId(id);

        Set<Long> reservationBookingIds = new HashSet<>();
        for (Reservation res : reservations) {
            if (res.getStatus() != ReservationStatus.CANCELLED) {
                res.setStatus(ReservationStatus.CANCELLED);
                reservationDao.update(res);
                reservationBookingIds.add(res.getBookingId());
            }
        }
        for (Long bookingId : reservationBookingIds) {
            if (bookingId != null) {
                kafkaTemplate.send("accommodation-cancel", new BookingEvent(bookingId, 0L, 0));
            }
        }
    }

    public void cancelReservationsByBookingId(Long bookingId) {
        List<Reservation> reservations = reservationDao.findAllByBookingId(bookingId);
        for (Reservation res : reservations) {
            if (res.getStatus() != ReservationStatus.CANCELLED) {
                res.setStatus(ReservationStatus.CANCELLED);
                reservationDao.update(res);
            }
        }
    }
}

