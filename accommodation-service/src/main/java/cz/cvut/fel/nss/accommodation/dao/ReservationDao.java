package cz.cvut.fel.nss.accommodation.dao;

import cz.cvut.fel.nss.entity.Reservation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
public class ReservationDao implements GenericDao<Reservation> {
    @PersistenceContext
    protected EntityManager em;

    @Override
    public Reservation find(Long id) {
        Objects.requireNonNull(id);
        return em.find(Reservation.class, id);
    }

    @Override
    public List<Reservation> findAll() {
        return em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
    }

    @Override
    public void save(Reservation entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public Reservation update(Reservation entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Reservation entity) {
        Objects.requireNonNull(entity);
        if (em.contains(entity)) {
            em.remove(entity);
            return;
        }
        final Reservation toRemove = em.find(Reservation.class, entity.getId());
        if (toRemove != null) {
            em.remove(toRemove);
        }
    }

    public List<Reservation> findIntersection(Long accommodationId, LocalDate from, LocalDate to) {
        return em.createNamedQuery("Reservation.findIntersection", Reservation.class)
                .setParameter("accommodationId", accommodationId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    public List<Reservation> findAllByAccommodationId(Long accommodationId) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.accommodation.id = :accommodationId", Reservation.class)
                .setParameter("accommodationId", accommodationId)
                .getResultList();
    }

    public List<Reservation> findAllByBookingId(Long bookingId) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.bookingId = :bookingId", Reservation.class)
                .setParameter("bookingId", bookingId)
                .getResultList();
    }
}
