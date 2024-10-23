package ru.itmo.is.lab1.filter;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.is.lab1.util.RoleAllowed;

import java.io.IOException;
import java.lang.reflect.Method;

@Provider
public class RoleAllowedFilter implements ContainerRequestFilter {
    @Inject
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();

        if (method.isAnnotationPresent(RoleAllowed.class)) {
            RoleAllowed roleAllowed = method.getAnnotation(RoleAllowed.class);

            String requiredRole = roleAllowed.value();
            SecurityContext securityContext = requestContext.getSecurityContext();

            if (!securityContext.isUserInRole(requiredRole)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("User does not have the required role: " + requiredRole)
                        .build());
            }
        }
    }
}
