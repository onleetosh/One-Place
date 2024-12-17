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


@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }


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
            System.err.println("Error retrieving shopping cart for user ID " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return cart;
    }


    @Override
    public void doPost(int userId, int productId)
    {
        // Check if the product is already in the cart.
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
                    try (PreparedStatement insertStmt = connection.prepareStatement(Queries.insertCart())) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, productId);
                        insertStmt.setInt(3, 1);  // 1 is quantity default
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Handle exceptions properly in production code
        }
    }


    /**
     * update the cart
     */
    @Override
    public void doPut(int userId, int productId, int quantity) {

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.updateShoppingCart())) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, userId);
            stmt.setInt(3, productId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating product quantity in cart", e);
        }
    }

    @Override
    public void doDelete(int userId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.dropShoppingCart())) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error clearing shopping cart", e);
        }
    }

    /**
     * Maps a ResultSet to a Shopping cart object.
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
