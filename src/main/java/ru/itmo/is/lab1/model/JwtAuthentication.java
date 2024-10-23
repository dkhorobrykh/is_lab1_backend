package ru.itmo.is.lab1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.security.Principal;

@AllArgsConstructor
@Getter
@Setter
public class JwtAuthentication implements Principal {
    private boolean authenticated;
    private boolean admin;
    private Long userId;

    @Override
    public String getName() {
        return "User_%s".formatted(userId);
    }
}
