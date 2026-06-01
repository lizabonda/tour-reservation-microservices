package cz.cvut.fel.nss.booking.dao;

import cz.cvut.fel.nss.booking.entity.Booking;
import cz.cvut.fel.nss.booking.entity.BookingStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
public class BookingDao implements GenericDao<Booking> {
    @PersistenceContext
    protected EntityManager em;

    @Override
    public Booking find(Long id) {
        Objects.requireNonNull(id);
        return em.find(Booking.class, id);
    }

    @Override
    public List<Booking> findAll() {
        return em.createQuery("SELECT b FROM Booking b", Booking.class).getResultList();
    }

    @Override
    public void save(Booking entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    public int nextBookingNumber() {
        Number value = (Number) em
                .createNativeQuery("SELECT nextval('booking_reservation_number_seq')")
                .getSingleResult();
        return value.intValue();
    }

    @Override
    public Booking update(Booking entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void refresh(Booking entity) {
        Objects.requireNonNull(entity);
        if (em.contains(entity)) {
            em.flush();
            em.refresh(entity);
        } else {
            Booking managed = em.merge(entity);
            em.flush();
            em.refresh(managed);
        }
    }

    @Override
    public List<Booking> findByUser(Long userId) {
        return em.createQuery("SELECT b FROM Booking b JOIN b.personIds personId WHERE personId=:userId", Booking.class)
                .setParameter("userId", userId).getResultList();
    }

    @Override
    public void remove(Booking entity) {
        Objects.requireNonNull(entity);
        if (em.contains(entity)) {
            em.remove(entity);
            return;
        }
        final Booking toRemove = em.find(Booking.class, entity.getId());
        if (toRemove != null) {
            em.remove(toRemove);
        }
    }

    public int countPersonsByTour(Long tourId) {
        Long count = em.createQuery(
                        "SELECT COUNT(p) " +
                                "FROM Booking b " +
                                "JOIN b.personIds p " +
                                "WHERE b.tourId = :tourId AND b.status != :status",
                        Long.class
                )
                .setParameter("tourId", tourId)
                .setParameter("status", BookingStatus.CANCELLED)
                .getSingleResult();

        return count.intValue();
    }

    public List<Booking> findBookingsCreatedBetween(LocalDate fromDate, LocalDate toDate) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> cq = cb.createQuery(Booking.class);
        Root<Booking> booking = cq.from(Booking.class);

        Predicate from = cb.greaterThanOrEqualTo(
                booking.get("createdAt"),
                fromDate
        );

        Predicate to = cb.lessThanOrEqualTo(
                booking.get("createdAt"),
                toDate
        );

        cq.select(booking)
                .where(cb.and(from, to))
                .orderBy(cb.asc(booking.get("createdAt")));

        return em.createQuery(cq).getResultList();
    }

    public List<Booking> findAllByTour(Long tourId) {
        return em.createQuery("SELECT b FROM Booking b WHERE b.tourId = :tourId", Booking.class)
                .setParameter("tourId", tourId)
                .getResultList();
    }
}