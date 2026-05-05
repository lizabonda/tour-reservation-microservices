package cz.cvut.fel.nss.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Booking {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_seq")
    @SequenceGenerator(name = "booking_seq", sequenceName = "booking_seq", allocationSize = 1)
    private Long id;

    @Column(name = "booking_number")
    private int bookingNumber;
    private double totalPrice;

    @NotNull
    @Column(nullable = false)
    private LocalDate createdAt;

    @NotNull
    @Column(nullable = false)
    private Long tourId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.CREATED;

    @ElementCollection
    @CollectionTable(name = "booking_person_ids", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "person_id")
    private List<Long> personIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "booking_reservation_ids", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "reservation_id")
    private List<Long> reservationIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "booking_activity_ids", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "activity_id")
    private List<Long> activityIds = new ArrayList<>();

    @OneToMany(mappedBy = "booking")
    @OrderBy("date")
    private List<Payment> payments = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(int bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public Long getTourId() {
        return tourId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public void setTourId(Long tourId) {
        this.tourId = tourId;
    }

    public List<Long> getPersonIds() {
        return personIds;
    }

    public void setPersonIds(List<Long> personIds) {
        this.personIds = personIds;
    }

    public List<Long> getReservationIds() {
        return reservationIds;
    }

    public void setReservationIds(List<Long> reservationIds) {
        this.reservationIds = reservationIds;
    }

    public List<Long> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<Long> activityIds) {
        this.activityIds = activityIds;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "reservationNumber=" + bookingNumber +
                ", totalPrice=" + totalPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking other = (Booking) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
