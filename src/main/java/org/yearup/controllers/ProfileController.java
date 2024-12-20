package org.yearup.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.interfaces.ProfileDao;
import org.yearup.data.interfaces.UserDao;
import org.yearup.data.mysql.MySqlProductDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;


@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController {

    private ProfileDao profileDao;
    private UserDao userDao;
    private static final Logger logger = LoggerFactory.getLogger(MySqlProductDao.class);

    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Profile> getCurrentUserProfile(Principal principal) {
        
        String username = principal.getName();
        User user = userDao.getCurrentUser(username);
        logger.debug("Fetching profile for user: {}", user);

        try {
            Profile profile = profileDao.getProfileById(user.getId());
            if (profile == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // No profile found
            }
            return ResponseEntity.ok(profile); // Return profile with HTTP 200 OK
        } catch (Exception ex) {
            logger.error("Error fetching profile for user {}: {}", username, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a profile by ID
     * @param id The ID of the profile to retrieve.
     * @return The profile object if found.
     * @throws ResponseStatusException if the profile is not found, returns HTTP 204 No Content.
     */
    @GetMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Profile> getById(@PathVariable int id) {
        try {
            Profile profile = profileDao.getProfileById(id);
            if (profile == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(profile);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates an existing profile with the data provided in the request body.
     * @param profile The profile object containing updated data.
     * @throws ResponseStatusException if the profile is not provided or is invalid.
     */

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Profile> putProfile(@RequestBody Profile profile, Principal principal) {
        String username = principal.getName();
        User user = userDao.getCurrentUser(username);
        logger.debug("Updating profile for user: {}", user);

        try {
            // Check if the profile exists
            Profile existingProfile = profileDao.getProfileById(user.getId());
            if (existingProfile == null) {
                logger.warn("Profile not found for ID: {}", user.getId());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            // Update the profile
            profileDao.update(profile, user);
            logger.info("Profile updated successfully for ID: {}", user.getId());

            // Fetch and return the updated profile
            Profile updatedProfile = profileDao.getProfileById(user.getId());
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception ex) {
            logger.error("Error updating profile: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
