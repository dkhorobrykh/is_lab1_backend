package ru.itmo.is.lab1.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link ru.itmo.is.lab1.model.AuditLog}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditLogDto implements Serializable {
    Long id;
    String tableName;
    String operation;
    Instant timestamp;
    UserDto user;
    String oldValue;
    String newValue;
}