package ru.itmo.is.lab1.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<String> errorList = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        ErrorDetails errorDetails = new ErrorDetails(
                Instant.now(),
                ExceptionEnum.BAD_REQUEST,
                errorList.toString()
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorDetails)
                .build();
    }
}
