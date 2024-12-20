package org.yearup.data.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.data.interfaces.ProductDao;
import org.yearup.models.Profile;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing Product-related operations in a MySQL database.
 * This class provides CRUD operations for products, including searching and filtering.
 */
@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao {
    private static final Logger logger = LoggerFactory.getLogger(MySqlProductDao.class);

    /**
     * Constructor for MySqlProductDao.
     * Initializes the DAO with the given DataSource for database connections.
     * @param dataSource The DataSource used to obtain database connections.
     */
    public MySqlProductDao(DataSource dataSource)
    {
        super(dataSource);
    }

    /**
     * Searches for products in the database based on category, price range, and color.
     * @param categoryId The category ID to filter products (optional).
     * @param minPrice The minimum price to filter products (optional).
     * @param maxPrice The maximum price to filter products (optional).
     * @param color The color to filter products (optional).
     * @return A list of products matching the given filters.
     */
    @Override
    public List<Product> search(Integer categoryId,
                                BigDecimal minPrice,
                                BigDecimal maxPrice,
                                String color) {
        // Declare an empty List to store the results
        List<Product> products = new ArrayList<>();

        // Establish a connection to the database
        try (Connection connection = getConnection()) {
            // Prepare the SQL statement
            PreparedStatement stmt = connection.prepareStatement(Queries.selectProductsByFilter());
            // Set the parameters to process the query
            stmt.setObject(1, categoryId == null ? -1 : categoryId); // categoryId
            stmt.setObject(2, categoryId == null ? -1 : categoryId); // categoryId (again)
            stmt.setObject(3, minPrice == null ? new BigDecimal("-1") : minPrice); // minPrice
            stmt.setObject(4, minPrice == null ? new BigDecimal("-1") : minPrice); // minPrice (again)
            stmt.setObject(5, maxPrice == null ? new BigDecimal("-1") : maxPrice); // maxPrice
            stmt.setObject(6, maxPrice == null ? new BigDecimal("-1") : maxPrice); // maxPrice (again)
            stmt.setObject(7, color == null ? "" : color); // color
            stmt.setObject(8, color == null ? "" : color); // color (again)
            ResultSet row = stmt.executeQuery(); // Execute the query
            // Map each row to a Product object and add it to the list
            while (row.next()) {
                Product product = mapRow(row);
                products.add(product);
            }
            logger.debug("Found {} products based on filters.", products.size());
        }
        catch (SQLException e) {
            // Throw a runtime exception if an SQL error occurs
            throw new RuntimeException("Error occurred filtering searching",e);
        }
        return products; // Return the list of matching products
    }

    /**
     * Retrieves all products from a specific category by category ID.
     * @param categoryId The category ID to filter products.
     * @return A list of products in the specified category.
     */
    @Override
    public List<Product> listByCategoryId(int categoryId) {
        // Declare a empty List to store the results
        List<Product> products = new ArrayList<>();
        // Establish a connection to the database
        try (Connection connection = getConnection()) {
            // Prepare the SQL statement and set the parameter to process the query
            PreparedStatement stmt = connection.prepareStatement(Queries.selectProductsByCatId());
            stmt.setInt(1, categoryId);

            // Execute the query and map each row to a Product object
            ResultSet row = stmt.executeQuery();
            while (row.next()) {
                Product product = mapRow(row);
                products.add(product);
            }
        }
        catch (SQLException e) {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException("Error occurred receiving product by category Id: " + categoryId,e);
        }
        return products; // Return the list of products in the specified category
    }

    /**
     * Retrieves a product by its product ID.
     *
     * @param productId The product ID to look up.
     * @return The product object corresponding to the given ID, or null if not found.
     */
    @Override
    public Product getById(int productId) {
        // Establish a connection to the database
        try (Connection connection = getConnection()) {
            // Prepare the SQL statement and set the parameter to process the query
            PreparedStatement statement = connection.prepareStatement(Queries.selectProductByProdId());
            statement.setInt(1, productId);
            // Execute the query and map each row to a Product object
            ResultSet row = statement.executeQuery();
            if (row.next()) {
                return mapRow(row);
            }
        }
        catch (SQLException e) {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException("Error occurred receiving product by product Id: " + productId,e);
        }
        return null; // Return null if the product is not found
    }


    /**
     * Creates a new product in the database.
     *
     * @param product The product object containing data to be inserted.
     * @return The created Product object, including the auto-generated ID.
     */
    @Override
    public Product create(Product product) {
        if (productExists(product.getName(), product.getCategoryId())) {
            throw new RuntimeException("A product with the same name and category already exists.");
        }
        // Establish a connection to the database
        try (Connection connection = getConnection()) {
            // Prepare the SQL statement and set the parameter to process the query
            PreparedStatement stmt = connection.prepareStatement(Queries.insertProduct(),
                    PreparedStatement.RETURN_GENERATED_KEYS);
            // set product parameters
            setProductParams(stmt, product);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = stmt.getGeneratedKeys();

                if (generatedKeys.next()) {
                    // Retrieve the auto-incremented ID
                    int orderId = generatedKeys.getInt(1);

                    // get the newly inserted category
                    return getById(orderId);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("An error occurred while creating the product.", e);
        }
        return null;
    }


    private boolean productExists(String name, int categoryId) {
        String sql = "SELECT COUNT(*) FROM products WHERE name = ? AND category_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking product existence.", e);
        }
        return false;
    }

    /**
     * Updates an existing product in the database.
     *
     * @param productId The ID of the product to be updated.
     * @param product The product object containing updated data.
     */
    @Override
    public void update(int productId, Product product) {
        try (Connection connection = getConnection()) {
            // Prepare the SQL statement and set the parameter to process the query
            PreparedStatement stmt = connection.prepareStatement(Queries.updateProductByProdId());

            // set product parameters
            setProductParams(stmt, product);

            // Set the product ID to update
            stmt.setInt(9, productId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating the product.", e);
        }
    }
    /**
     * Helper method to set the parameters for the PreparedStatement.
     */
    private void setProductParams(PreparedStatement stmt, Product product) throws SQLException {
        stmt.setString(1, product.getName());
        stmt.setBigDecimal(2, product.getPrice());
        stmt.setInt(3, product.getCategoryId());
        stmt.setString(4, product.getDescription());
        stmt.setString(5, product.getColor());
        stmt.setString(6, product.getImageUrl());
        stmt.setInt(7, product.getStock());
        stmt.setBoolean(8, product.isFeatured());
    }


    /**
     * Deletes a product from the database by its product ID.
     *
     * @param productId The ID of the product to delete.
     */

    @Override
    public void delete(int productId) {
        // Establish a connection to the database
        try (Connection connection = getConnection()) {
            // Prepare the SQL statement and set the parameter to process the query
            PreparedStatement stmt = connection.prepareStatement(Queries.dropProductById());
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Maps a ResultSet row to a Product object.
     *
     * @param row The ResultSet object containing a row of product data.
     * @return A Product object populated with data from the ResultSet.
     * @throws SQLException If an error occurs while reading the ResultSet.
     */
    protected static Product mapRow(ResultSet row) throws SQLException {
        return new Product(
                row.getInt("product_id"),
                row.getString("name"),
                row.getBigDecimal("price"),
                row.getInt("category_id"),
                row.getString("description"),
                row.getString("color"),
                row.getInt("stock"),
                row.getBoolean("featured"),
                row.getString("image_url")
        );
    }
}
