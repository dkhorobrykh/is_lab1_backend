package ru.itmo.is.lab1.repository;

import ru.itmo.is.lab1.model.AuditLog;

import java.util.Optional;

public class AuditLogRepository extends AbstractRepository<AuditLog, Long> {
    public AuditLogRepository() {
        super(AuditLog.class);
    }

    public Optional<AuditLog> findLastByEntityId(Long entityId) {
        return entityManager.createQuery(
                "SELECT al FROM AuditLog al WHERE al.entityId = :entityId ORDER BY al.id DESC", AuditLog.class)
                .setParameter("entityId", entityId)
                .getResultStream()
                .findFirst();
    }
}
