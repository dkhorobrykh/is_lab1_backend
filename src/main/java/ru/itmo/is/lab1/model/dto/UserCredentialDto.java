package ru.itmo.is.lab1.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Data
@Builder
@Jacksonized
public class UserCredentialDto implements Serializable {
    private String username;
    private String password;
}
