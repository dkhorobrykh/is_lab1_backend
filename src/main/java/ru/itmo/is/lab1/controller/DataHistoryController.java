package ru.itmo.is.lab1.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.model.mapper.AuditLogMapper;
import ru.itmo.is.lab1.service.AuditLogService;
import ru.itmo.is.lab1.util.RoleAllowed;

@Path("/audit")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class DataHistoryController {
    @Inject
    private AuditLogService auditLogService;

    @Inject
    private AuditLogMapper auditLogMapper;

    @GET
    @RoleAllowed("ADMIN")
    public Response getAllAuditData() {
        var result = auditLogService.getAll();

        return Response.ok(auditLogMapper.toDto(result)).build();
    }
}
