package ru.itmo.is.lab1.model.mapper;

import org.mapstruct.*;
import ru.itmo.is.lab1.model.AuditLog;
import ru.itmo.is.lab1.model.dto.AuditLogDto;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.CDI, uses = {UserMapper.class})
public interface AuditLogMapper {
    AuditLog toEntity(AuditLogDto auditLogDto);

    AuditLogDto toDto(AuditLog auditLog);

    List<AuditLogDto> toDto(List<AuditLog> auditLogs);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AuditLog partialUpdate(AuditLogDto auditLogDto, @MappingTarget AuditLog auditLog);
}