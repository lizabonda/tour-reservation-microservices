package cz.cvut.fel.nss.accommodation.service;

import cz.cvut.fel.nss.accommodation.facade.AccommodationDaoFacade;
import cz.cvut.fel.nss.accommodation.kafka.AccommodationEventPublisher;
import cz.cvut.fel.nss.accommodation.entity.Accommodation;
import cz.cvut.fel.nss.accommodation.entity.MealPlan;
import cz.cvut.fel.nss.accommodation.entity.Reservation;
import cz.cvut.fel.nss.accommodation.dto.AccommodationDto;
import cz.cvut.fel.nss.accommodation.dto.AccommodationPricingSummaryDto;
import cz.cvut.fel.nss.accommodation.dto.ReservationDto;
import cz.cvut.fel.nss.accommodation.dto.mapper.AccommodationMapper;
import cz.cvut.fel.nss.accommodation.dto.mapper.ReservationMapper;
import cz.cvut.fel.nss.accommodation.entity.ReservationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AccommodationService {

    private static final double ALL_INCLUSIVE_PERCENT = 0.15;

    private final AccommodationDaoFacade accommodationDaoFacade;
    private final AccommodationMapper accommodationMapper;
    private final ReservationMapper reservationMapper;
    private final AccommodationEventPublisher accommodationEventPublisher;

    public AccommodationService(AccommodationDaoFacade accommodationDaoFacade, AccommodationMapper accommodationMapper, ReservationMapper reservationMapper, AccommodationEventPublisher accommodationEventPublisher) {
        this.accommodationDaoFacade = accommodationDaoFacade;
        this.accommodationMapper = accommodationMapper;
        this.reservationMapper = reservationMapper;
        this.accommodationEventPublisher = accommodationEventPublisher;
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

            Accommodation accommodation = accommodationDaoFacade.findAccommodationById(reservationDto.accommodationId());

            Reservation reservation = reservationMapper.reservationDtoToReservation(reservationDto);
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
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking id must not be null");
        }

        return reservationsDto.stream().map(dto -> {
            if (dto == null) {
                throw new IllegalArgumentException("Reservation must not be null");
            }
            if (dto.id() != null) {
                throw new IllegalArgumentException("Reservation id must be null on create");
            }
            if (dto.bookingId() != null) {
                throw new IllegalArgumentException("Reservation bookingId must be null on create");
            }
            if (dto.startDate() == null || dto.endDate() == null) {
                throw new IllegalArgumentException("Reservation must have startDate and endDate");
            }
            if (dto.accommodationId() == null) {
                throw new IllegalArgumentException("Reservation must have accommodation.id");
            }
            if (dto.reservationPrice() != 0.0) {
                throw new IllegalArgumentException("Reservation price must not be provided on create");
            }

            Accommodation accommodation = accommodationDaoFacade.findAccommodationById(dto.accommodationId());

            Reservation reservation = reservationMapper.reservationDtoToReservation(dto);
            reservation.setAccommodation(accommodation);
            reservation.setBookingId(bookingId);
            reservation.calculateReservationPrice();
            reservation.validate();

            if (accommodationDaoFacade.hasIntersection(
                    accommodation.getId(),
                    reservation.getStartDate(),
                    reservation.getEndDate(),
                    null
            )) {
                throw new IllegalStateException("Accommodation is not available for given dates");
            }

            accommodationDaoFacade.saveReservation(reservation);
            return reservation;
        }).toList();
    }

    public void updateBookingAccommodation(Long reservationId, Long newaccommodationId) {
        if (newaccommodationId == null) {
            throw new IllegalArgumentException("New accommodation id must not be null");
        }

        Reservation r = accommodationDaoFacade.findReservationById(reservationId);

        Accommodation newAccommodation = accommodationDaoFacade.findAccommodationById(newaccommodationId);

        r.setAccommodation(newAccommodation);
        r.calculateReservationPrice();
        r.validate();

        if (accommodationDaoFacade.hasIntersection(newaccommodationId, r.getStartDate(), r.getEndDate(), reservationId)) {
            throw new IllegalStateException("Accommodation is not available for given dates");
        }

        accommodationDaoFacade.updateReservation(r);
    }

    public Accommodation createAccommodation(AccommodationDto accommodationDto) {
        Accommodation accommodation = accommodationMapper.accommodationDtoToAccommodation(accommodationDto);
        accommodationDaoFacade.saveAccommodation(accommodation);
        return accommodation;
    }

    public void deleteAccommodation(Long id) {
        Accommodation accommodation = accommodationDaoFacade.findAccommodationById(id);
        accommodation.setDeleted(true);
        accommodationDaoFacade.updateAccommodation(accommodation);

        List<Reservation> reservations = accommodationDaoFacade.findAllReservationsByAccommodationId(id);

        Set<Long> reservationBookingIds = new HashSet<>();

        for (Reservation res : reservations) {
            if (res.getStatus() != ReservationStatus.CANCELLED) {
                res.setStatus(ReservationStatus.CANCELLED);
                accommodationDaoFacade.updateReservation(res);
                reservationBookingIds.add(res.getBookingId());
            }
        }

        for (Long bookingId : reservationBookingIds) {
            accommodationEventPublisher.publishAccommodationCancelled(bookingId);
        }
    }

    public void cancelReservationsByBookingId(Long bookingId) {
        accommodationDaoFacade.findAllReservationsByBookingId(bookingId).stream()
                .filter(res -> res.getStatus() != ReservationStatus.CANCELLED)
                .forEach(res -> {
                    res.setStatus(ReservationStatus.CANCELLED);
                    accommodationDaoFacade.updateReservation(res);
                });
    }
}

