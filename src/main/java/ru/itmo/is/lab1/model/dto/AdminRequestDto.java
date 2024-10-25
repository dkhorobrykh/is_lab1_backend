package ru.itmo.is.lab1.model.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link ru.itmo.is.lab1.model.AdminRequest}
 */
@Data
@Builder
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
public class AdminRequestDto implements Serializable {
    Long id;
    UserDto user;
    Instant createdDatetime;
    boolean approved;
    boolean active;
}