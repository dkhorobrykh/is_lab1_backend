package ru.itmo.is.lab1.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.model.JwtAuthentication;
import ru.itmo.is.lab1.model.User;
import ru.itmo.is.lab1.service.UserService;

import java.util.Optional;

@ApplicationScoped
@Slf4j
public class JwtUtil {
    @Inject
    private UserService userService;

    public JwtAuthentication generateAuth(Claims claims) {

        User user = userService.getById(Long.parseLong(claims.get("userId").toString()));

        return new JwtAuthentication(
                true,
                user.isAdmin(),
                user.getId()
        );
    }
}
