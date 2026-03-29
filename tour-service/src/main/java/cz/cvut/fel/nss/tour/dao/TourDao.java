package cz.cvut.fel.nss.tour.dao;

import cz.cvut.fel.nss.projekt.model.Tour;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
public class TourDao implements  GenericDao<Tour> {
    @PersistenceContext
    protected EntityManager em;

    @Override
    public Tour find(Long id) {
        Objects.requireNonNull(id);
        return em.find(Tour.class, id);
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

    public Tour findByDestinationAndStartDate(String destination, LocalDate startDate) {
        return em.createNamedQuery("Tour.findByDestinationAndStartDate", Tour.class)
                .setParameter("destination", destination)
                .setParameter("date", startDate)
                .getSingleResult();
    }
}
