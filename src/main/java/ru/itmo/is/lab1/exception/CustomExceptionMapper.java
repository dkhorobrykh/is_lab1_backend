package ru.itmo.is.lab1.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.AccessDeniedException;
import java.time.Clock;
import java.time.Instant;

@Provider
@Slf4j
public class CustomExceptionMapper implements ExceptionMapper<CustomException> {

    @Context
    private UriInfo uriInfo;

    public Response toResponse(CustomException ex) {
        ExceptionEnum exceptionEnum = ex.getExceptionEnum();

        log.warn("CustomException occurred: {}", exceptionEnum, ex);

        return Response.status(exceptionEnum.getStatus())
                .entity(new ErrorDetails(
                        Instant.now(),
                        exceptionEnum,
                        uriInfo != null ? uriInfo.getRequestUri().toString() : "Unknown URI"
                ))
                .build();
    }

//    public Response handleAuthorizationException(AccessDeniedException ex) {
//        log.warn("Access denied for request to: {}",
//                uriInfo != null ? uriInfo.getRequestUri().toString() : "Unknown URI", ex);
//
//        ExceptionEnum exConstant = ExceptionEnum.AUTHORIZATION_ERROR;
//
//        return Response.status(exConstant.getStatus())
//                .entity(new ErrorDetails(
//                        Instant.now(),
//                        exConstant,
//                        uriInfo != null ? uriInfo.getRequestUri().toString() : "Unknown URI"
//                ))
//                .build();
//    }
//
//    public Response handleServerException(Exception ex) {
//        log.error("Server error occurred", ex);
//        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                .entity(new ErrorDetails(
//                        Instant.now(),
//                        ExceptionEnum.SERVER_ERROR,
//                        uriInfo != null ? uriInfo.getRequestUri().toString() : "Unknown URI"
//                ))
//                .build();
//    }
//
//    public Response handleOtherExceptions(Throwable ex) {
//        log.error("Unhandled exception occurred", ex);
//        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                .entity(new ErrorDetails(
//                        Instant.now(),
//                        ExceptionEnum.SERVER_ERROR,
//                        uriInfo != null ? uriInfo.getRequestUri().toString() : "Unknown URI"
//                ))
//                .build();
//    }

}
