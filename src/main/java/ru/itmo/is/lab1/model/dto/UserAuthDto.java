package ru.itmo.is.lab1.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthDto implements Serializable {
    private String username;
    private String password;
}
