/**
 * This interface defines the contract for user-related database operations.
 * It provides methods to retrieve, create, and check the existence of user data.
 * Implementations of this interface handle user information such as user ID, username,
 * and other user-related details in the underlying data store.
 */
package org.yearup.data.interfaces;

import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.User;

import java.util.List;

public interface UserDao {

    /**
     * Retrieves all users in the system.
     * @return A list of all users.
     */
    List<User> getAll();

    /**
     * Retrieves a user by their unique ID.
     * @param userId The unique identifier of the user.
     * @return The user with the specified ID, or null if not found.
     */
    User getUserById(int userId);

    /**
     * Retrieves a user by their username.
     * @param username The username of the user.
     * @return The user with the specified username, or null if not found.
     */
    User getByUserName(String username);

    /**
     * Retrieves the unique ID associated with a given username.
     * @param username The username of the user.
     * @return The ID of the user, or -1 if the user is not found.
     */

    int getIdByUsername(String username);
    /**
     * Creates a new user in the system.
     * @param user The user object to be created.
     * @return The created user with an assigned ID.
     */
    User create(User user);

    /**
     * Checks if a user with a given username already exists in the system.
     * @param username The username to check.
     * @return True if the user exists, otherwise false.
     */
    boolean exists(String username);

    /**
     * Retrieve the currently logged-in user from the database.
     *
     * @param username The username of the logged-in user.
     * @return The User object.
     * @throws ResponseStatusException if the user is not found.
     */

    User getCurrentUser(String username);
}
