package org.yearup.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Component responsible for creating, validating, and parsing JWT tokens.
 * Implements InitializingBean to perform key initialization after property injection.
 */
@Component
public class TokenProvider implements InitializingBean
{

    // Logger instance for logging token-related information.
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    // Key used for storing authorities in the JWT token.
    private static final String AUTHORITIES_KEY = "auth";

    // Secret key for signing the JWT token.
    private final String secret;

    // Token timeout duration in milliseconds.
    private final long tokenTimeout;

    // Key instance for signing the JWT token.
    private Key key;


    /**
     * Constructor for TokenProvider.
     *
     * @param secret             The secret key used for signing the token (Base64-encoded).
     * @param tokenTimeoutSeconds The token expiration time in seconds.
     */
    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-timeout-seconds}") long tokenTimeoutSeconds)
    {
        this.secret = secret;
        this.tokenTimeout = tokenTimeoutSeconds * 1000;
    }

    /**
     * Initializes the signing key after all properties are set.
     * Decodes the Base64-encoded secret key and creates an HMAC key.
     */
    @Override
    public void afterPropertiesSet()
    {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Creates a JWT token for the given authentication object.
     *
     * @param authentication The authentication object containing user details.
     * @param rememberMe     Whether to extend the token expiration time.
     * @return A signed JWT token as a String.
     */
    public String createToken(Authentication authentication, boolean rememberMe)
    {
        // Extract authorities and convert to a comma-separated string
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date expirationDate = new Date(now + this.tokenTimeout);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expirationDate)
                .compact();
    }
    /**
     * Parses a JWT token to extract user authentication details.
     *
     * @param token The JWT token to parse.
     * @return An Authentication object containing the user details and authorities.
     */
    public Authentication getAuthentication(String token)
    {
        // Parse claims from the token
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        // Extract authorities from claims and convert to GrantedAuthority objects
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // Create a User principal from the claims
        User principal = new User(claims.getSubject(), "", authorities);

        // Return an authentication token
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * Validates the given JWT token.
     *
     * @param authToken The JWT token to validate.
     * @return True if the token is valid; false otherwise.
     */
    public boolean validateToken(String authToken)
    {
        try
        {
            // Parse and validate the token
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        }
        catch (Exception e)
        {
            // Log invalid token errors
            logger.info("Token Invalid.");
            logger.trace("Token Invalid trace: {}.", e.toString());
        }
        return false;
    }
}
