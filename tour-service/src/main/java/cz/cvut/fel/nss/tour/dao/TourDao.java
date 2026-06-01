package cz.cvut.fel.nss.tour.dao;

import cz.cvut.fel.nss.tour.entity.Tour;
import cz.cvut.fel.nss.tour.entity.TourStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
public class TourDao implements  GenericDao<Tour> {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Tour find(Long id) {
        Objects.requireNonNull(id);
        Tour tour = em.find(Tour.class, id);
        return tour;
    }

    @Override
    public List<Tour> findAll() {
        return em.createQuery("SELECT t FROM Tour t", Tour.class).getResultList();
    }

    @Override
    public void save(Tour entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    @Override
    public Tour update(Tour entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void refresh(Tour entity) {
        Objects.requireNonNull(entity);
        if (em.contains(entity)) {
            em.flush();
            em.refresh(entity);
        } else {
            Tour managed = em.merge(entity);
            em.flush();
            em.refresh(managed);
        }
    }

    @Override
    public void remove(Tour entity) {
        Objects.requireNonNull(entity);
        if (em.contains(entity)) {
            em.remove(entity);
            return;
        }
        final Tour toRemove = em.find(Tour.class, entity.getId());
        if (toRemove != null) {
            em.remove(toRemove);
        }
    }

    @Override
    public List<Tour> findByDate(LocalDate startDate, LocalDate endDate) {
        return em.createQuery("SELECT t FROM Tour t WHERE t.startDate >= :startDate\n" +
                        "                  AND t.endDate <= :endDate AND t.status = :status", Tour.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("status", TourStatus.ACTIVE)
                .getResultList();
    }

    public Tour findByDestinationAndStartDate(String destination, LocalDate startDate) {
        List<Tour> result = em.createNamedQuery("Tour.findByDestinationAndStartDate", Tour.class)
                .setParameter("destination", destination)
                .setParameter("date", startDate)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }
}
