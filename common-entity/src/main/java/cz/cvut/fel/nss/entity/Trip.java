package cz.cvut.fel.nss.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
@Entity
public class Trip {
    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Column(nullable = false)
    private String carrier;
    @NotNull
    @Column(nullable = false)
    private LocalDateTime departAt;
    @NotNull
    @Column(nullable = false)
    private LocalDateTime arriveAt;
    @NotNull
    @Column(name = "from_location", nullable = false)
    private String from;
    @NotNull
    @Column(name = "to_location", nullable = false)
    private String to;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    public String getCarrier() {
        return carrier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public LocalDateTime getDepartAt() {
        return departAt;
    }

    public void setDepartAt(LocalDateTime departAt) {
        this.departAt = departAt;
    }

    public LocalDateTime getArriveAt() {
        return arriveAt;
    }

    public void setArriveAt(LocalDateTime arriveAt) {
        this.arriveAt = arriveAt;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public TransportType getType() {
        return type;
    }

    public void setType(TransportType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "carrier='" + carrier + '\'' +
                ", departAt=" + departAt +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip)) return false;
        Trip other = (Trip) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
