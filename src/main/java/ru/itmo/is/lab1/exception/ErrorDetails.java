package ru.itmo.is.lab1.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Data
@Slf4j
@Setter
@Getter
@NoArgsConstructor
public class ErrorDetails {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String details;

    public ErrorDetails(Instant timestamp, ExceptionEnum constant, String details) {
        this.timestamp = timestamp;
        this.error = constant.getError();
        this.message = constant.getMessage();
        this.details = details;
        this.status = constant.getStatus().getStatusCode();
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            log.error("Error generating JSON object ErrorDetails: {}", ex.getMessage());
            throw new CustomException(ExceptionEnum.SERVER_ERROR);
        }
    }
}
