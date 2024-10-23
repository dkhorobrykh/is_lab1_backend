package ru.itmo.is.lab1.model.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link ru.itmo.is.lab1.model.User}
 */
@Value
public class UserDto implements Serializable {
    Long id;
    String username;
    String name;
    boolean admin;
}