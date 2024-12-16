package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

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
        ShoppingCart shoppingCart = new ShoppingCart();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.selectProductByUserId())) {

            // Set the userId in the query
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Retrieve product details
                    int productId = rs.getInt("product_id");
                    String name = rs.getString("name");
                    BigDecimal price = rs.getBigDecimal("price");
                    int categoryId = rs.getInt("category_id");
                    String description = rs.getString("description");
                    String color = rs.getString("color");
                    int stock = rs.getInt("stock");
                    boolean isFeatured = rs.getBoolean("featured");
                    String imageUrl = rs.getString("image_url");
                    int quantity = rs.getInt("quantity");

                    // Create Product and ShoppingCartItem objects
                    Product product = new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);
                    ShoppingCartItem item = new ShoppingCartItem(product, quantity, BigDecimal.ZERO); // Set discountPercent to 0
                    shoppingCart.add(item); // Add the item to the shopping cart
                }
            }
        } catch (SQLException e) {
            // Log the exception with a meaningful message
            System.err.println("Error retrieving shopping cart for user ID " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return shoppingCart;
    }

    @Override
    public void addToCart(int userId, int productId)
    {
        // Check if the product is already in the cart.
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.selectQuantity())) {

            stmt.setInt(1, userId);
            stmt.setInt(2, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // If the product is already in the cart, update the quantity
                    int value = rs.getInt("quantity");
                    int incrementValue = value + 1;  // Increment the quantity by 1 (or any desired value)

                    try (PreparedStatement updateStmt = connection.prepareStatement(Queries.updateShoppingCart())) {
                        updateStmt.setInt(1, incrementValue);
                        updateStmt.setInt(2, userId);
                        updateStmt.setInt(3, productId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // If the product is not in the cart, insert a new record
                    String insertSql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, productId);
                        insertStmt.setInt(3, 1);  // 1 is quantity default
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void updateCart(int userId, int productId, int quantity) {

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
    public void emptyCart(int userId) {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.dropShoppingCart())) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error clearing shopping cart", e);
        }
    }


}
