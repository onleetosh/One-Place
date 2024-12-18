/**
 * This interface defines the contract for operations related to user profiles.
 * It provides methods for creating, retrieving, and updating a user's profile information.
 * Implementations of this interface interact with the underlying data storage to manage
 * user profile data, such as address, contact information, and other personal details.
 */
package org.yearup.data.interfaces;

import org.yearup.models.Profile;

public interface ProfileDao {

    /**
     * Create a new user profile and insert it into the database.
     *
     * @param profile The Profile object containing the details of the user profile to be created.
     * @return The created Profile object, potentially with a generated ID.
     */
    Profile create(Profile profile);

    /**
     * Retrieve a user profile by its unique ID.
     *
     * @param id The ID of the profile to retrieve.
     * @return The Profile object with the specified ID, or null if no profile is found.
     */
    Profile getProfileById(int id);

    /**
     * Update the details of an existing user profile.
     *
     * @param profile The Profile object containing the updated details of the user profile.
     */
    void update(Profile profile);

}
