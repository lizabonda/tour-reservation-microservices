package cz.cvut.fel.nss.accommodation.service;

//import cz.cvut.fel.nss.accommodation.client.BookingClient;
import cz.cvut.fel.nss.entity.Accommodation;
import cz.cvut.fel.nss.entity.MealPlan;
import cz.cvut.fel.nss.entity.Reservation;
import cz.cvut.fel.nss.accommodation.dao.AccommodationDao;
import cz.cvut.fel.nss.accommodation.dao.ReservationDao;
import cz.cvut.fel.nss.accommodation.dto.AccommodationDto;
import cz.cvut.fel.nss.accommodation.dto.AccommodationPricingSummaryDto;
import cz.cvut.fel.nss.accommodation.dto.ReservationDto;
import cz.cvut.fel.nss.accommodation.dto.mapper.AccommodationMapper;
import cz.cvut.fel.nss.accommodation.exception.NotFoundException;
import cz.cvut.fel.nss.entity.ReservationStatus;
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
//    private final BookingClient bookingClient;
    private final KafkaTemplate<String, Long> kafkaTemplate;

    public AccommodationService(AccommodationDao accommodationDao, ReservationDao reservationDao, AccommodationMapper accommodationMapper,  KafkaTemplate<String, Long> kafkaTemplate) {
        this.accommodationDao = accommodationDao;
        this.reservationDao = reservationDao;
        this.accommodationMapper = accommodationMapper;
//        this.bookingClient = bookingClient;
        this.kafkaTemplate = kafkaTemplate;
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

            accommodationPrice += reservation.getReservationPrice();
        }

        return new AccommodationPricingSummaryDto(accommodationPrice);
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
            int persons = reservationDto.numberOfPersons() > 0 ? reservationDto.numberOfPersons() : 1;
            reservation.setNumberOfPersons(persons);
            reservation.calculateReservationPrice();
            validationForReservation(reservation);
            
            if (accommodation.getCapacity() < persons) {
                throw new IllegalStateException("Not enough capacity in accommodation");
            }
            accommodation.setCapacity(accommodation.getCapacity() - persons);
            accommodationDao.update(accommodation);

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

        Accommodation oldAccommodation = r.getAccommodation();
        int persons = r.getNumberOfPersons() > 0 ? r.getNumberOfPersons() : 1;
        if (oldAccommodation != null) {
            oldAccommodation.setCapacity(oldAccommodation.getCapacity() + persons);
            accommodationDao.update(oldAccommodation);
        }
        if (newAccommodation.getCapacity() < persons) {
            throw new IllegalStateException("Not enough capacity in new accommodation");
        }
        newAccommodation.setCapacity(newAccommodation.getCapacity() - persons);
        accommodationDao.update(newAccommodation);

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
            throw new RuntimeException("Accommodation not found: " + id);
        }
        accommodation.setDeleted(true);
        accommodationDao.update(accommodation);

        List<Reservation> reservations = reservationDao.findAllByAccommodationId(id);

        Set<Long> reservationBookingIds = new HashSet<>();
        for (Reservation res : reservations) {
            if (res.getStatus() != ReservationStatus.CANCELLED) {
                res.setStatus(ReservationStatus.CANCELLED);
                int persons = res.getNumberOfPersons() > 0 ? res.getNumberOfPersons() : 1;
                accommodation.setCapacity(accommodation.getCapacity() + persons);
                reservationDao.update(res);
                reservationBookingIds.add(res.getBookingId());
            }
        }
        accommodationDao.update(accommodation);
        for (Long bookingId : reservationBookingIds) {
            if (bookingId != null) {
                kafkaTemplate.send("accommodation-cancel", bookingId);
//                try {
//                    bookingClient.removeBookingByIdInternally(bookingId);
//                } catch (feign.FeignException.NotFound e) {
//                    //TODO
//                }
            }
        }
    }

    public void cancelReservationsByBookingId(Long bookingId) {
        List<Reservation> reservations = reservationDao.findAllByBookingId(bookingId);
        for (Reservation res : reservations) {
            if (res.getStatus() != ReservationStatus.CANCELLED) {
                res.setStatus(ReservationStatus.CANCELLED);
                Accommodation accommodation = res.getAccommodation();
                if (accommodation != null) {
                    int persons = res.getNumberOfPersons() > 0 ? res.getNumberOfPersons() : 1;
                    accommodation.setCapacity(accommodation.getCapacity() + persons);
                    accommodationDao.update(accommodation);
                }
                reservationDao.update(res);
            }
        }
    }
}

