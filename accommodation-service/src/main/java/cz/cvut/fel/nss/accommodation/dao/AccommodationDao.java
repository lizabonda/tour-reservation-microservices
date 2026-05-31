package cz.cvut.fel.nss.accommodation.dao;

import cz.cvut.fel.nss.accommodation.Accommodation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class AccommodationDao implements GenericDao<Accommodation> {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Accommodation find(Long id) {
        Objects.requireNonNull(id);
        Accommodation accommodation = em.find(Accommodation.class, id);
        if (accommodation != null && accommodation.isDeleted()) {
            return null;
        }
        return accommodation;
    }

    @Override
    public List<Accommodation> findAll() {
        return em.createQuery("SELECT a FROM Accommodation a WHERE a.deleted = false", Accommodation.class).getResultList();
    }

    @Override
    public void save(Accommodation entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);

    }

    @Override
    public Accommodation update(Accommodation entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Accommodation entity) {
        Objects.requireNonNull(entity);
        if (em.contains(entity)) {
            em.remove(entity);
            return;
        }
        final Accommodation toRemove = em.find(Accommodation.class, entity.getId());
        if (toRemove != null) {
            em.remove(toRemove);
        }
}}
