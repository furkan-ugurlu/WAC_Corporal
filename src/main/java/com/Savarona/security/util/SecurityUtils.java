package com.Savarona.security.util;

import com.Savarona.security.model.SecurityUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * Gets the currently authenticated user
     */
    public static SecurityUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof SecurityUser) {
            return (SecurityUser) principal;
        }
        throw new IllegalStateException("Not authenticated");
    }

    /**
     * Checks if there is an authenticated user
     */
    public static boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SecurityUser;
    }
}