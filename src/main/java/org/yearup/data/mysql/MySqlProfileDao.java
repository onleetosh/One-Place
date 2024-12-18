package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.interfaces.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;
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
    public Profile create(Profile profile)
    {
        // Establish a connection to the database
        try(Connection connection = getConnection())
        {
            // Prepare the SQL statement to insert a new profile into the database.
            PreparedStatement ps = connection.prepareStatement(Queries.insertProfile(), PreparedStatement.RETURN_GENERATED_KEYS);
            // Set the values for the prepared statement.
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());
            ps.executeUpdate(); // Execute query.
            // Retrieving generated keys (auto-increment ID)
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Assuming that your profile class has a setter for ID
                    profile.setUserId(generatedKeys.getInt(1));
                }
            }

            return profile; // Return the updated Profile object.
        }
        catch (SQLException e)
        {
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
        try(Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    Queries.selectProfileById()))
        {
            // Set the ID parameter for the query
            stmt.setInt(1, userId);
            // Execute the query and process the results
            try( ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    int id = rs.getInt(1);
                    String firstName = rs.getString(2);
                    String lastName = rs.getString(3);
                    String phone = rs.getString(4);
                    String email = rs.getString(5);
                    String address = rs.getString(6);
                    String city = rs.getString(7);
                    String state = rs.getString(8);
                    String zip = rs.getString(9);
                    // Map the result set to a Profile object and return it.
                    return new Profile(id,
                            firstName,
                            lastName,
                            phone,
                            email,
                            address,
                            city,
                            state,
                            zip
                    );
                }
            }
        } catch (SQLException e) {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException("Error retrieving profile for user ID " + userId, e);
        }
        return null; // Return null if no profile was found.
    }

    /**
     * Updates an existing profile in the database.
     *
     * @param profile The Profile object containing updated information.
     */
    @Override
    public void update(Profile profile) {

        // Establish a connection to the database
        try (Connection connection = getConnection())
        {
            // Prepare the SQL statement to insert a new profile into the database.
            PreparedStatement stmt = connection.prepareStatement(Queries.updateProfile());
            // Set the values for the prepared statement.
            stmt.setString(1, profile.getFirstName());
            stmt.setString(2, profile.getLastName());
            stmt.setString(3, profile.getPhone());
            stmt.setString(4, profile.getEmail());
            stmt.setString(5, profile.getAddress());
            stmt.setString(6, profile.getCity());
            stmt.setString(7, profile.getState());
            stmt.setString(8, profile.getZip());
            stmt.setInt(9, profile.getUserId());
            // Execute the query.
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException("Error updating profile for user ID " + profile.getUserId(), e);
        }
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
