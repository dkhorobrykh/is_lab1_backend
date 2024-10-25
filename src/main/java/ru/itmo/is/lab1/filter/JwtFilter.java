package ru.itmo.is.lab1.filter;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.config.SecurityContextHolder;
import ru.itmo.is.lab1.exception.CustomException;
import ru.itmo.is.lab1.exception.ExceptionEnum;
import ru.itmo.is.lab1.model.JwtAuthentication;
import ru.itmo.is.lab1.service.JwtProvider;
import ru.itmo.is.lab1.util.JwtUtil;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Provider
@Priority(Priorities.AUTHORIZATION)
public class JwtFilter implements ContainerRequestFilter {

    @Inject
    private JwtProvider jwtProvider;

    @Inject
    private JwtUtil jwtUtil;

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/vehicle",
            "/auth/login",
            "/auth/register"
    );

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
//        try {

        String path = containerRequestContext.getUriInfo().getPath();

        if (isExcludedPath(path))
            return;

        String authHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        String token = authHeader.substring(7);
        try {
            jwtProvider.validateAccessToken(token);
        } catch (Exception exception) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        Claims claims;

        try {
            claims = jwtProvider.getAccessClaims(token);
        } catch (Exception ex) {
            log.error("{}", ex.toString());
            throw new CustomException(ExceptionEnum.TOKEN_CHECKING_ERROR);
        }

        JwtAuthentication jwtAuthentication = jwtUtil.generateAuth(claims);

        SecurityContext securityContext = new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return jwtAuthentication;
            }

            @Override
            public boolean isUserInRole(String s) {
                if ("ADMIN".equals(s)) {
                    return ((JwtAuthentication) getUserPrincipal()).isAdmin();
                }
                return ((JwtAuthentication) getUserPrincipal()).isAuthenticated();
            }

            @Override
            public boolean isSecure() {
                return containerRequestContext.getSecurityContext().isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return "Bearer";
            }
        };
        SecurityContextHolder.setContext(securityContext);
        containerRequestContext.setSecurityContext(securityContext);
//        } finally {
//            SecurityContextHolder.clear();
//        }
    }

    private boolean isExcludedPath(String path) {
        return path.contains("/vehicle/query/")
                || EXCLUDED_PATHS.stream().anyMatch(p -> p.equals(path));
    }
}
