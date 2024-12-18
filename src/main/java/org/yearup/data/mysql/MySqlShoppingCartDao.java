package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.interfaces.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.cart.ShoppingCart;
import org.yearup.models.cart.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO implementation for managing shopping cart data in a MySQL database.
 */
@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    /**
     * Constructor for MySqlShoppingCartDao.
     * @param dataSource The DataSource to be used for database connections.
     */
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Retrieves the shopping cart for a specific user ID.
     *
     * @param userId The ID of the user whose shopping cart needs to be fetched.
     * @return The ShoppingCart object containing the items for the user.
     */
    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.selectCartByUserId())) {
            // Set the userId in the query
            stmt.setInt(1, userId);

            try (ResultSet row = stmt.executeQuery()) {
                while (row.next()) {
                    ShoppingCartItem item = mapRow(row);
                    cart.add(item);
                }
            }
        } catch (SQLException e) {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException("Error retrieving shopping cart for user ID " + userId + ": " + e);
        }
        return cart;
    }


    /**
     * Adds a product to the user's shopping cart. If the product already exists, increments the quantity.
     *
     * @param userId    The ID of the user.
     * @param productId The ID of the product to be added.
     */
    @Override
    public void post(int userId, int productId)
    {
        // SQL query to check if the product is already in the cart
        String checkSql = Queries.selectQuantity();

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(checkSql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // If the product is already in the cart, update the quantity
                    int value = rs.getInt("quantity");
                    int updateQuantity = value + 1;  // Increment the quantity by 1 (or any desired value)

                    try (PreparedStatement updateStmt = connection.prepareStatement(Queries.updateShoppingCart())) {
                        updateStmt.setInt(1, updateQuantity);
                        updateStmt.setInt(2, userId);
                        updateStmt.setInt(3, productId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // If the product is not in the cart, insert new record
                    try (PreparedStatement insertStmt = connection.prepareStatement(Queries.insertShoppingCart())) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, productId);
                        insertStmt.setInt(3, 1);  // 1 is quantity default
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException("Error inserting product",e);
        }
    }


    /**
     * Updates the quantity of a product in the shopping cart.
     *
     * @param userId    The ID of the user.
     * @param productId The ID of the product to be updated.
     * @param quantity  The new quantity for the product.
     */
    @Override
    public void update(int userId, int productId, int quantity) {

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.updateShoppingCart())) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, userId);
            stmt.setInt(3, productId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            // Handle SQL exceptions by throwing a RuntimeException
            throw new RuntimeException("Error updating product quantity in cart", e);
        }
    }
    /**
     * Deletes all items from the shopping cart for a specific user.
     *
     * @param userId The ID of the user whose cart needs to be cleared.
     */
    @Override
    public void delete(int userId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.dropShoppingCart())) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error clearing shopping cart", e);
        }
    }

    /**
     * Maps a ResultSet row to a ShoppingCartItem object.
     *
     * @param row The ResultSet containing the data to map.
     * @return A ShoppingCartItem object representing the row data.
     * @throws SQLException If an error occurs while accessing the ResultSet.
     */
    protected ShoppingCartItem mapRow(ResultSet row) throws SQLException {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String color = row.getString("color");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");
        int quantity = row.getInt("quantity");

        // Create Product object
        Product product = new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);

        // Create and return ShoppingCartItem with discountPercent set to 0 by default
        return new ShoppingCartItem(product, quantity, BigDecimal.ZERO);
    }

}
