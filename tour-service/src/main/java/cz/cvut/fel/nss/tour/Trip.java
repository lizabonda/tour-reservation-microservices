package cz.cvut.fel.nss.tour;

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
    String carrier;
    @NotNull
    @Column(nullable = false)
    LocalDateTime departAt;
    @NotNull
    @Column(nullable = false)
    LocalDateTime arriveAt;
    @NotNull
    @Column(name = "from_location", nullable = false)
    String from;
    @NotNull
    @Column(name = "to_location", nullable = false)
    String to;
    @Enumerated(value=EnumType.STRING)
    TransportType type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    public String getCarrier() {
        return carrier;
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
                ", tour=" + tour +
                '}';
    }
}
