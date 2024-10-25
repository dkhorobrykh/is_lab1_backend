package ru.itmo.is.lab1.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.config.SecurityContextHolder;
import ru.itmo.is.lab1.exception.CustomException;
import ru.itmo.is.lab1.exception.ExceptionEnum;
import ru.itmo.is.lab1.repository.AuditLogRepository;
import ru.itmo.is.lab1.service.RoleService;

import java.time.Instant;

@ApplicationScoped
@Slf4j
public class AuditListener {

    @Inject
    private RoleService roleService;

    @Inject
    private AuditLogRepository auditLogRepository;

    private static final String INSERT = "INSERT";
    private static final String UPDATE = "UPDATE";
    private static final String DELETE = "DELETE";

    @PrePersist
    public void prePersist(Object entity) {
        createAuditLog(entity.getClass(), entity, INSERT, null, entity);
    }

    @PreUpdate
    public void preUpdate(Object entity) {
//        var oldEntity = findOldEntity(entity);
//        if (oldEntity != null && !isDifferent(oldEntity, entity)) {
//            return;
//        }
        createAuditLog(entity.getClass(), entity, UPDATE, null, entity);
    }

    @PreRemove
    public void preRemove(Object entity) {
        createAuditLog(entity.getClass(), entity, DELETE, entity, null);
    }

    private boolean isDifferent(Object oldEntity, Object newEntity) {
        return !oldEntity.equals(newEntity);
    }

    private void createAuditLog(Class<?> entityClass, Object entity, String operation, Object oldValue, Object newValue) {
        log.info("Old Value: {}", entityClass.cast(newValue));  // Отладка
        log.info("New Value: {}", newValue);  // Отладка

        SecurityContext securityContext = SecurityContextHolder.getContext();

        AuditLog audit = new AuditLog();

        audit.setTableName(entity.getClass().getSimpleName());
        audit.setOperation(operation);
        audit.setEntityId(getEntityId(entity));
        audit.setTimestamp(Instant.now());
        audit.setUser(securityContext == null ? null : roleService.getCurrentUser(securityContext));
        audit.setOldValue(oldValue == null ? null : serializeToJson(entityClass.cast(oldValue)));
        audit.setNewValue(newValue == null ? null : serializeToJson(entityClass.cast(newValue)));

        auditLogRepository.save(audit);
    }

    private String serializeToJson(Object obj) {
        try {
            return new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            log.error("Serialization error: {}", ex.getMessage());
            throw new CustomException(ExceptionEnum.SERVER_ERROR);
        }
    }

    private Long getEntityId(Object entity) {
        try {
            Object id = entity.getClass().getMethod("getId").invoke(entity);

            if (id == null) return null;

            if (id instanceof Integer) {
                return (long) (Integer) id;
            } else if (id instanceof Long) {
                return (Long) id;
            } else {
                log.error("Unsupported ID type: {}", id.getClass().getName());
                throw new CustomException(ExceptionEnum.SERVER_ERROR);
            }
        } catch (Exception ex) {
            log.error("GetEntityId error: {}", ex.getMessage());
            throw new CustomException(ExceptionEnum.SERVER_ERROR);
        }
    }

    private Object findOldEntity(Object entity) {
        return auditLogRepository.findLastByEntityId(getEntityId(entity)).orElse(null);
    }
}
