package ru.itmo.is.lab1.model.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * DTO for {@link ru.itmo.is.lab1.model.User}
 */
@Data
@Builder
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {
    Long id;
    String username;
    String name;
    boolean admin;
}