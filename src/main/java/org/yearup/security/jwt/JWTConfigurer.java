/**
 * A security configuration class to integrate JWT filtering into the Spring Security framework.
 * This class extends SecurityConfigurerAdapter to configure custom security filters.
 */
package org.yearup.security.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    // A reference to the TokenProvider, responsible for managing and validating JWT tokens.
    private TokenProvider tokenProvider;

    /**
     * Constructor for JWTConfigurer.
     *
     * @param tokenProvider The TokenProvider instance used to manage and validate JWT tokens.
     */
    public JWTConfigurer(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }


    /**
     * Configures the HttpSecurity object by adding a custom JWT filter.
     * The JWTFilter is applied before the UsernamePasswordAuthenticationFilter in the security filter chain.
     *
     * @param http The HttpSecurity object to be configured.
     */
    @Override
    public void configure(HttpSecurity http) {
        // Create an instance of the custom JWTFilter, passing the TokenProvider
        JWTFilter customFilter = new JWTFilter(tokenProvider);
        // Add the JWT filter to the security filter chain before the default authentication filter
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
