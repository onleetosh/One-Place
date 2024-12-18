package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.interfaces.UserDao;
import org.yearup.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Data Access Object (DAO) for managing User-related operations in a MySQL database.
 * This class provides CRUD operations for users.
 */
@Component
public class MySqlUserDao extends MySqlDaoBase implements UserDao {

    /**
     * Constructor for MySqlUserDao.
     * @param dataSource The DataSource used to obtain database connections.
     */
    @Autowired
    public MySqlUserDao(DataSource dataSource)
    {
        super(dataSource);
    }

    /**
     * Creates a new user in the database.
     * The password is hashed before storing it.
     * @param newUser The User object containing the user data to be created.
     * @return The created User object without the password.
     */
    @Override
    public User create(User newUser) {
        // Hash the user's password using BCrypt
        String hashedPassword = new BCryptPasswordEncoder().encode(newUser.getPassword());

        // Establish a connection to the database
        try (Connection connection = getConnection())
        {
            //Prepare the SQL statement and generate auto key
            PreparedStatement ps = connection.prepareStatement(Queries.insertUsers(),
                    Statement.RETURN_GENERATED_KEYS);
            // Set the parameters for the query
            ps.setString(1, newUser.getUsername());
            ps.setString(2, hashedPassword);
            ps.setString(3, newUser.getRole());
            ps.executeUpdate();  // Execute the query
            // Retrieve the user from the database by username
            User user = getByUserName(newUser.getUsername());
            user.setPassword(""); // Clear the password before returning the user
            return user; // Return the user without the password
        }
        catch (SQLException e)
        {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all users from the database.
     * @return A list of all users in the database.
     */
    @Override
    public List<User> getAll() {
        // Declare an empty List to store results
        List<User> users = new ArrayList<>();
        // Establish a connection to the database
        try (Connection connection = getConnection())
        {
            //Prepare the SQL statement
            PreparedStatement statement = connection.prepareStatement(Queries.selectUsers());
            // Execute the query and process the result set
            ResultSet row = statement.executeQuery();

            // Map each row to a User object and add it to the list
            while (row.next()) {
                User user = mapRow(row);
                users.add(user);
            }
        }
        catch (SQLException e) {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException("Error receiving users", e);
        }
        return users; // Return the list of users
    }
    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The User object corresponding to the given ID, or null if not found.
     */
    @Override
    public User getUserById(int id) {
        // Establish a connection to the database
        try (Connection connection = getConnection())
        {
            //Prepare the SQL statement and set the parameter
            PreparedStatement statement = connection.prepareStatement(Queries.selectUsersById());
            statement.setInt(1, id);
            // Execute the query, map the results to a User object and return it
            ResultSet row = statement.executeQuery();
            if(row.next())
            {
                User user = mapRow(row);
                return user;
            }
        }
        catch (SQLException e) {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException("Error receiving user by userId: " + id, e);
        }

        return null; // Return null if the user is not found
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return The User object corresponding to the given username, or null if not found.
     */
    @Override
    public User getByUserName(String username) {
        // Establish a connection to the database
        try (Connection connection = getConnection())
        {
            //Prepare the SQL statement and set the parameter
            PreparedStatement statement = connection.prepareStatement(Queries.selectUsersByName());
            statement.setString(1, username);

            // Execute the query, map the results to a User object and return it
            ResultSet row = statement.executeQuery();
            if(row.next())
            {
                User user = mapRow(row);
                return user;
            }
        }
        catch (SQLException e) {
           throw new RuntimeException("Error receiving user by username: " + username,e);
        }
        return null;// Return null if the user is not found
    }

    /**
     * Retrieves the user ID by their username.
     *
     * @param username The username of the user to retrieve the ID for.
     * @return The user ID, or -1 if the user does not exist.
     */
    @Override
    public int getIdByUsername(String username) {
        User user = getByUserName(username);
        if(user != null) {
            return user.getId();
        }
        return -1;
    }

    /**
     * Checks if a user with the given username exists in the database.
     *
     * @param username The username to check.
     * @return true if the user exists, false otherwise.
     */
    @Override
    public boolean exists(String username) {
        User user = getByUserName(username);
        return user != null;
    }
    /**
     * Maps a ResultSet to a User object.
     * @param row The ResultSet object containing the query result.
     * @return A User object populated with data from the ResultSet.
     * @throws SQLException If an error occurs while reading the ResultSet.
     */
    private User mapRow(ResultSet row) throws SQLException {
        int userId = row.getInt("user_id");
        String username = row.getString("username");
        String hashedPassword = row.getString("hashed_password");
        String role = row.getString("role");
        // Create a new User object using the retrieved data
        return new User(userId, username,hashedPassword, role);
    }
}
