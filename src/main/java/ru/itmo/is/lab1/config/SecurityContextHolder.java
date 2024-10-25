package ru.itmo.is.lab1.config;


import jakarta.ws.rs.core.SecurityContext;

public class SecurityContextHolder {
    private static final ThreadLocal<SecurityContext> CONTEXT = new ThreadLocal<>();

    public static void setContext(SecurityContext securityContext) {
        CONTEXT.set(securityContext);
    }

    public static SecurityContext getContext() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
