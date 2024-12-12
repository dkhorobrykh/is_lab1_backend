package ru.itmo.is.lab1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.Optional;

public abstract class AbstractRepository<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityClass;

    protected AbstractRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    public T save(@Valid T entity) {
        return entityManager.merge(entity);
    }

    public T update(@Valid T entity) {
        return entityManager.merge(entity);
    }

    @Transactional
    public void delete(T entity) {
        T managedEntity = update(entity);
        entityManager.remove(managedEntity);
    }
}
