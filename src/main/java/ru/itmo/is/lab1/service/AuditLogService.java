package ru.itmo.is.lab1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.model.AuditLog;
import ru.itmo.is.lab1.repository.AuditLogRepository;

import java.util.List;

@ApplicationScoped
@Slf4j
public class AuditLogService {
    @Inject
    private AuditLogRepository auditLogRepository;

    public List<AuditLog> getAll() {
        return auditLogRepository.getAll();
    }
}
