package cz.cvut.fel.nss.booking.dao;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


public interface GenericDao<T> {


    T find(Long id);

    List<T> findAll();

    void save(T entity);

    default void persist(Collection<T> entities) {
        Objects.requireNonNull(entities);
        entities.forEach(this::save);
    }

    T update(T entity);

    void remove(T entity);

    default boolean exists(Long id) {
        return id != null && find(id) != null;
    }
}
