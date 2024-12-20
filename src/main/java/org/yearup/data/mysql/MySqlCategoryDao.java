package org.yearup.data.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yearup.data.interfaces.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing Category-related operations
 * in a MySQL database. This class provides CRUD operations for categories.
 */
@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    private static final Logger logger = LoggerFactory.getLogger(MySqlCategoryDao.class);

    /**
     * Constructor for MySqlCategoryDao.
     *
     * @param dataSource The DataSource used to obtain database connections.
     */
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return A list of Category objects representing all categories.
     */
    @Override
    public List<Category> getAllCategories() {
        // Declare an empty List to store the results
        List<Category> category = new ArrayList<>();

        // Establish a connection to database,
        // Prepare the SQL statement,
        // and Execute query
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.selectCategories());
             ResultSet results = stmt.executeQuery()) {
            // Process each row in the result set
            while (results.next()) {
                Category cat = new Category(
                        results.getInt(1), //category_id
                        results.getString(2), //name
                        results.getString(3) //description
                );
                category.add(cat);
                logger.debug("Added Category: {}", cat);
            }
        } catch (SQLException e) {
            // Wrap and rethrow any SQL exceptions
            logger.error("Error retrieving categories", e);
            throw new RuntimeException("Error retrieving categories", e);
        }
        return category; // Return null if no category is found
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param categoryId The ID of the category to retrieve.
     * @return A Category object representing the requested category, or null if not found.
     */
    @Override
    public Category getById(int categoryId) {
        // establish a connection to database,
        // prepare the SQL statement,
        try(Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    Queries.selectCategoriesByCatId())) {
            // Set the ID parameter for the query
            stmt.setInt(1, categoryId);
            // Execute the query and process the results
            try( ResultSet results = stmt.executeQuery()){
                while (results.next()){
                    return new Category(
                            results.getInt(1), // category_id
                            results.getString(2), // name
                            results.getString(3) // description
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving category by ID: {}", categoryId, e);
            // Wrap and rethrow any SQL exceptions
            throw new RuntimeException("Error retrieving category by ID: " + categoryId, e);
        }
        return null; // Return null if no category is found
    }

    /**
     * Retrieves a category by its name.
     *
     * @param name The name of the category to retrieve.
     * @return A Category object representing the requested category, or null if not found.
     */
    @Override
    public Category getByName(String name) {

        // establish a connection to database.
        // prepare the SQL statement.
        try(Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    Queries.selectCategoriesByName())) {
            // Set the ID parameter for the query
            stmt.setString(1, name);
            // Execute the query and process the results
            try( ResultSet results = stmt.executeQuery()){
                while (results.next()){
                    return new Category(
                            results.getInt(1), //category_id
                            results.getString(2), //name
                            results.getString(3) //description
                    );
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error retrieving category by name: {}", name, e);
            // Wrap and rethrow any SQL exceptions
            throw new RuntimeException("Error retrieving category by name: " + name, e);
        }
        return null; // Return null if no category is found
    }

    /**
     * Inserts a new category into the database.
     *
     * @param category The Category object containing name and description.
     * @return The Category object with the generated ID.
     */
    @Override
    public Category create(Category category) {
        // establish a connection to database.
        // prepare the SQL statement.
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.insertCategories(), PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Set the parameter for the query
            stmt.setString(1, category.getName()); //name
            stmt.setString(2, category.getDescription()); //description
            // Execute the query
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            // Retrieve generated ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setCategoryId(generatedKeys.getInt(1));
                }
            }
        }
        catch (SQLException e) {
            logger.error("Error inserting category", e);
            // Wrap and rethrow any SQL exceptions
            throw new RuntimeException("Error inserting category", e);
        }

        return category;
    }

    /**
     * Updates an existing category based on its ID.
     *
     * @param categoryId The ID of the category to update.
     * @param category   The updated Category object with new values.
     */
    @Override
    public void update(int categoryId, Category category)
    {
        // establish a connection to database.
        // prepare the SQL statement.
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.updateCategoriesByCatId())) {
            // Set the parameter for the query
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, categoryId);
            // Execute the update query
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                // Wrap and rethrow any SQL exceptions
                throw new SQLException("Update failed, no rows affected.");
            }
        } catch (SQLException e) {
            logger.error("Error updating category with ID: {}", categoryId, e);
            // Wrap and rethrow any SQL exceptions
            throw new RuntimeException("Error updating category with ID: " + categoryId, e);
        }
    }

    /**
     * Deletes a category by its ID.
     *
     * @param categoryId The ID of the category to delete.
     */
    @Override
    public void delete(int categoryId)
    {
        // establish a connection to database.
        // prepare the SQL statement.
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.dropCategoriesByCatId())) {
            // Set the parameters for the query
            stmt.setInt(1, categoryId);
            // Execute the update query
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Delete failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category with ID: " + categoryId, e);
        }
    }


    /**
     *  method to check if a category exists.
     */
    @Override
    public boolean categoryExist(String name) {
        return getByName(name) != null;
    }


    /**
     * Utility method to map a single ResultSet row to a Category object.
     *
     * @param row The ResultSet object containing the query results.
     * @return A Category object populated with data from the current row.
     * @throws SQLException If an error occurs while reading from the ResultSet.
     */
    protected static Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
