package cz.cvut.fel.nss.user.dao;

import cz.cvut.fel.nss.user.entity.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class PersonDao implements GenericDao<Person> {
    @PersistenceContext
    protected EntityManager em;

    @Override
    public Person find(Long id) {
        Objects.requireNonNull(id);
        return em.find(Person.class, id);
    }

    @Override
    public List<Person> findAll() {
        return em.createQuery("SELECT p FROM Person p", Person.class).getResultList();
    }

    @Override
    public void save(Person entity) {
        Objects.requireNonNull(entity);
        em.persist(entity);
    }

    public Person findByDetails(String firstName, String lastName, java.time.LocalDate dateOfBirth) {
        List<Person> results = em.createQuery(
                        "SELECT p FROM Person p WHERE p.firstName = :firstName AND p.lastName = :lastName AND p.dateOfBirth = :dateOfBirth",
                        Person.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .setParameter("dateOfBirth", dateOfBirth)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public Person update(Person entity) {
        Objects.requireNonNull(entity);
        return em.merge(entity);
    }

    @Override
    public void remove(Person entity) {
        Objects.requireNonNull(entity);
        if (em.contains(entity)) {
            em.remove(entity);
            return;
        }
        final Person toRemove = em.find(Person.class, entity.getId());
        if (toRemove != null) {
            em.remove(toRemove);
        }
    }
}
