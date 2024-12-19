package org.yearup.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityUtils.class);

    private SecurityUtils() {
    }

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * This method checks the security context to get the authentication object,
     * and based on the principal, extracts the username.
     *
     * @return an Optional containing the username of the current user if present,
     *         or an empty Optional if no authentication information is available.
     */
    public static Optional<String> getCurrentUsername() {
        // Get the authentication object from the SecurityContextHolder
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If no authentication is present, log and return empty Optional
        if (authentication == null) {
            LOG.debug("no authentication context found");
            return Optional.empty();
        }

        // Variable to hold the username
        String username = null;
        // Check if the principal is an instance of UserDetails (common in Spring Security)
        if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        LOG.debug("found username '{}' in context", username);

        // Return the username wrapped in an Optional (it could be null, hence Optional)
        return Optional.ofNullable(username);
    }
}
