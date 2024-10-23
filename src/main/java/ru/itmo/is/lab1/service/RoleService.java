package ru.itmo.is.lab1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.exception.CustomException;
import ru.itmo.is.lab1.exception.ExceptionEnum;
import ru.itmo.is.lab1.model.JwtAuthentication;
import ru.itmo.is.lab1.model.User;

@RequestScoped
@Slf4j
public class RoleService {

    @Inject
    private UserService userService;

    public User getCurrentUser(SecurityContext securityContext) {
        JwtAuthentication currentUser = (JwtAuthentication) securityContext.getUserPrincipal();
        if (currentUser == null)
            throw new CustomException(ExceptionEnum.FORBIDDEN);

        return userService.getById(currentUser.getUserId());
    }
}
