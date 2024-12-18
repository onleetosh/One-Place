package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.interfaces.ProfileDao;
import org.yearup.models.Profile;

import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController {

    private ProfileDao profileDao;


    public ProfileController(ProfileDao profileDao)
    {
        this.profileDao = profileDao;
    }


    /**
     * Retrieves a profile by ID
     * @param id The ID of the profile to retrieve.
     * @return The profile object if found.
     * @throws ResponseStatusException if the profile is not found, returns HTTP 204 No Content.
     */
    @GetMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public Profile getById(@PathVariable int id)
    {
        Profile profile = profileDao.getProfileById(id);
        // Check if the profile is null or doesn't exist
        if(profile == null || !profileExist(profile)) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Profile not found");
        }
        return profile;
    }

    /**
     * Updates an existing profile with the data provided in the request body.
     * @param profile The profile object containing updated data.
     * @throws ResponseStatusException if the profile is not provided or is invalid.
     */
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public void putProfile(@RequestBody Profile profile)
    {
        // Check if the profile is null or doesn't exist
        if(profile == null || !profileExist(profile)) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Profile not found");
        }
        // process update
        profileDao.update(profile);
    }

    // Method to check if the profile exists in the database
    private boolean profileExist(Profile profile) {
        Profile existingProfile = profileDao.getProfileById(profile.getUserId());
        return existingProfile != null;
    }

}
