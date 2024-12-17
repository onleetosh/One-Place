package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.interfaces.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile)
    {
        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(Queries.insertProfile(), PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            // Retrieving generated keys if you need them, e.g., auto-increment ID
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Assuming that your profile class has a setter for ID
                    profile.setUserId(generatedKeys.getInt(1));
                }
            }

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile getProfileById(int userId) {

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
                    String lastName =  rs.getString(3);
                    String phone = rs.getString(4);
                    String email =  rs.getString(5);
                    String address =  rs.getString(6);
                    String city =  rs.getString(7);
                    String state =  rs.getString(8);
                    String zip = rs.getString(9);

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
            throw new RuntimeException(e);
        }
        return null;

    }

    @Override
    public void updateProfile(Profile profile) {

        try (Connection connection = getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(Queries.updateProfile());
            stmt.setString(1, profile.getFirstName());
            stmt.setString(2, profile.getLastName());
            stmt.setString(3, profile.getPhone());
            stmt.setString(4, profile.getEmail());
            stmt.setString(5, profile.getAddress());
            stmt.setString(6, profile.getCity());
            stmt.setString(7, profile.getState());
            stmt.setString(8, profile.getZip());
            stmt.setInt(9, profile.getUserId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Maps a ResultSet to a Profile object.
     */
    protected static Profile mapRow(ResultSet row) throws SQLException
    {
        int userId = row.getInt("user_id");
        String firstName = row.getString("first_name");
        String lastName = row.getString("last_name");
        String phone = row.getString("phone");
        String email = row.getString("email");
        String address = row.getString("address");
        String city = row.getString("city");
        String state = row.getString("state");
        String zip = row.getString("zip");

        return new Profile(userId, firstName, lastName, phone, email, address, city, state, zip);
    }
}
