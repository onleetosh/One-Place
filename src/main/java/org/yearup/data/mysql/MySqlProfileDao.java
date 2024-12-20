package org.yearup.data.mysql;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Profile;
import org.yearup.data.interfaces.ProfileDao;
import org.yearup.models.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) responsible for handling database operations related to user profiles.
 * This class implements the ProfileDao interface and interacts with a MySQL database.
 */
@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    /**
     * Constructor for MySqlProfileDao.
     *
     * @param dataSource The DataSource to provide database connections.
     */
    public MySqlProfileDao(DataSource dataSource) {
        super(dataSource);
    }


    /**
     * Inserts a new profile record into the database.
     *
     * @param profile The Profile object containing the user profile data to be saved.
     * @return The saved Profile object with an updated userId if generated automatically.
     */
    @Override
    public Profile create(Profile profile) {
        // Establish a connection to the database
        try (Connection connection = getConnection()) {
            // Prepare the SQL statement to insert a new profile into the database.
            PreparedStatement ps = connection.prepareStatement(Queries.insertProfile(), PreparedStatement.RETURN_GENERATED_KEYS);
            // Set the values for the prepared statement using the helper method
            setProfileParams(ps, profile);
            // Execute query.
            ps.executeUpdate();
            // Retrieve the generated keys (auto-increment ID)
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Set the generated userId (auto-incremented ID) in the profile
                    profile.setUserId(generatedKeys.getInt(1));
                }
            }

            return profile; // Return the updated Profile object.
        } catch (SQLException e) {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException("Error creating profile", e);
        }
    }
    /**
     * Retrieves a user's profile by their userId.
     *
     * @param userId The ID of the user whose profile is to be retrieved.
     * @return The Profile object containing user data, or null if not found.
     */
    @Override
    public Profile getProfileById(int userId) {
        // Establish a connection to the database and prepare the SQL statement
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.selectProfileById())) {

            // Set the ID parameter for the query
            stmt.setInt(1, userId);

            // Execute the query and process the results
            try (ResultSet row = stmt.executeQuery()) {
                if (row.next()) {
                    // Use mapRow method to convert the ResultSet to a Profile object
                    return mapRow(row);
                }
            }
        } catch (SQLException e) {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException("Error retrieving profile for user ID " + userId, e);
        }

        // Return null if no profile was found
        return null;
    }


    /**
     * Retrieve the user's profile from the database to fetch address details.
     *
     * @param user The logged-in User object.
     * @return The Profile object containing user address and contact information.
     * @throws ResponseStatusException if the profile is not found.
     */
    @Override
    public Profile getUserProfile(User user) {
        Profile profile = getProfileById(user.getId());
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User profile not found");
        }
        return profile;
    }

    /**
     * Updates an existing profile in the database.
     *
     * @param profile The Profile object containing updated information.
     */
    @Override
    public void update(Profile profile, User user) {

        // Establish a connection to the database
        try (Connection connection = getConnection()) {
            // Prepare the SQL statement to update the profile in the database
            PreparedStatement stmt = connection.prepareStatement(Queries.updateProfile());
            // Set the values for the prepared statement
            setProfileParams(stmt, profile);
            stmt.setInt(9, user.getId());
            // Execute the update query
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException("Error updating profile for user ID " + profile.getUserId(), e);
        }
    }


    /**
     * Helper method to set the parameters for the PreparedStatement.
     */
    private void setProfileParams(PreparedStatement stmt, Profile profile) throws SQLException {
        stmt.setString(1, profile.getFirstName());
        stmt.setString(2, profile.getLastName());
        stmt.setString(3, profile.getPhone());
        stmt.setString(4, profile.getEmail());
        stmt.setString(5, profile.getAddress());
        stmt.setString(6, profile.getCity());
        stmt.setString(7, profile.getState());
        stmt.setString(8, profile.getZip());
    }

    /**
     * Maps a ResultSet row to a Profile object.
     *
     * @param row The ResultSet containing profile data.
     * @return A Profile object created from the ResultSet data.
     * @throws SQLException If an error occurs while accessing the ResultSet.
     */
    protected static Profile mapRow(ResultSet row) throws SQLException
    {
        // Extract column values from the ResultSet.
        int userId = row.getInt("user_id");
        String firstName = row.getString("first_name");
        String lastName = row.getString("last_name");
        String phone = row.getString("phone");
        String email = row.getString("email");
        String address = row.getString("address");
        String city = row.getString("city");
        String state = row.getString("state");
        String zip = row.getString("zip");
        // Create and return a Profile object with the extracted data.
        return new Profile(userId, firstName, lastName, phone, email, address, city, state, zip);
    }
}
