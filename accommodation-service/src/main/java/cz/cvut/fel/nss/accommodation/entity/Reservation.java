package cz.cvut.fel.nss.accommodation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@NamedQuery(
        name = "Reservation.findIntersection",
        query = "SELECT r from Reservation r " +
                "WHERE r.accommodation.id = :accommodationId " +
                "AND r.accommodation.deleted = false " +
                "AND r.endDate > :from " +
                "AND r.startDate < :to " +
                "AND r.status != :status " +
                "ORDER BY r.startDate"
)
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_seq")
    @SequenceGenerator(name = "reservation_seq", sequenceName = "reservation_seq", allocationSize = 1)
    private Long id;
    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;
    @NotNull
    @Column(nullable = false)
    private LocalDate endDate;
    private double reservationPrice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @NotNull
    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private int numberOfPersons;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.CREATED;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void validate() {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Reservation must have startDate and endDate");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("Reservation endDate must be after startDate");
        }
        if (accommodation == null) {
            throw new IllegalArgumentException("Reservation must have accommodation");
        }
        if (numberOfPersons <= 0) {
            throw new IllegalArgumentException("Number of persons must be positive");
        }
        if (numberOfPersons > accommodation.getCapacity()) {
            throw new IllegalStateException("Too many persons for this accommodation");
        }
    }

    public double getReservationPrice() {
        return reservationPrice;
    }

    public void setReservationPrice(double reservationPrice) {
        this.reservationPrice = reservationPrice;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public int getNumberOfPersons() {
        return numberOfPersons;
    }

    public void setNumberOfPersons(int numberOfPersons) {
        this.numberOfPersons = numberOfPersons;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void calculateReservationPrice() {
        long nights = ChronoUnit.DAYS.between(startDate, endDate);
        if (nights <= 0) {
            throw new IllegalArgumentException("Reservation endDate must be after startDate");
        }
        setReservationPrice(accommodation.getPricePerNight() * nights);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", reservationPrice=" + reservationPrice +
                ", accommodation=" + accommodation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation other = (Reservation) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
