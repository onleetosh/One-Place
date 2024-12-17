package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.interfaces.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        List<Category> category = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.selectCategories());
             ResultSet results = stmt.executeQuery()) {

            while (results.next()) {
                Category cat = new Category(
                        results.getInt(1),
                        results.getString(2),
                        results.getString(3)
                );
                category.add(cat);
                System.out.println("Added Category: " + cat);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Total categories fetched: " + category.size());
        return category;
    }

    @Override
    public Category getById(int categoryId)
    {
        try(Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    Queries.selectCategoriesByCatId()))
        {
            // Set the ID parameter for the query
            stmt.setInt(1, categoryId);
            // Execute the query and process the results
            try( ResultSet results = stmt.executeQuery()){
                while (results.next()){
                    return new Category(
                            results.getInt(1),
                            results.getString(2),
                            results.getString(3)
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Category getByName(String name) {


        try(Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    Queries.selectCategoriesByName()))
        {
            // Set the ID parameter for the query
            stmt.setString(1, name);
            // Execute the query and process the results
            try( ResultSet results = stmt.executeQuery()){
                while (results.next()){
                    return new Category(
                            results.getInt(1),
                            results.getString(2),
                            results.getString(3)
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Category createCategory(Category category)
    {

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.insertCategories(), PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Set the parameter for the query
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());


            // Execute the query
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }

            // Retrieve generated keys
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setCategoryId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Insert failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting category", e);
        }

        return category;
    }

    @Override
    public void update(int categoryId, Category category)
    {

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.updateCategoriesByCatId())) {

            // Set the parameter for the query
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, categoryId);

            // Execute the update query
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating category", e);
        }
    }

    @Override
    public void delete(int categoryId)
    {

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.dropCategoriesByCatId())) {

            // Set the parameters for the query
            stmt.setInt(1, categoryId);

            // Execute the update query
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating category", e);
        }
    }

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
