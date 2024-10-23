package ru.itmo.is.lab1.exception;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Provider
@Slf4j
public class GenericExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(RuntimeException ex) {
        log.error("Unhandled exception occurred", ex);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorDetails(
                        Instant.now(),
                        ExceptionEnum.SERVER_ERROR,
                        uriInfo.getRequestUri().toString()
                ))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
