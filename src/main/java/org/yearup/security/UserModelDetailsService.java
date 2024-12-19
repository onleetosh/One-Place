package org.yearup.security;


import org.yearup.data.interfaces.UserDao;
import org.yearup.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserModelDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserModelDetailsService.class);

    private final UserDao userDao;

    public UserModelDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Loads the user details by username for authentication.
     *
     * This method is called by Spring Security when attempting to authenticate a user.
     * It retrieves the user details from the database using the provided username (login),
     * and creates a Spring Security User object with the authorities.
     *
     * @param login the username provided during authentication.
     * @return a fully populated UserDetails object for Spring Security.
     */
    @Override
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating user '{}'", login);
        String lowercaseLogin = login.toLowerCase();
        return createSpringSecurityUser(lowercaseLogin, userDao.getByUserName(lowercaseLogin));
    }

    /**
     * Creates a Spring Security User object from a custom User entity.
     *
     * This method maps the custom User object (from the database) to a Spring Security
     * User object with appropriate authorities (roles/permissions) for authentication.
     *
     * @param lowercaseLogin the lowercase username (for logging purposes).
     * @param user the user object retrieved from the database.
     * @return a Spring Security User object.
     * @throws UserNotActivatedException if the user is not activated.
     */
    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {
        if (!user.isActivated()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                grantedAuthorities);
    }
}

