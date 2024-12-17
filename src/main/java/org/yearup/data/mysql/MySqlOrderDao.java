package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.interfaces.OrderDao;
import org.yearup.models.Product;
import org.yearup.models.Profile;
import org.yearup.models.cart.ShoppingCart;
import org.yearup.models.cart.ShoppingCartItem;
import org.yearup.models.order.Order;
import org.yearup.models.order.OrderLineItem;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;


@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {


    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Order postOrder(Order order, Profile profile, ShoppingCart cart) {

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.insertOrder(), PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Set the parameters for the order statement
            stmt.setInt(1, order.getUserId()); // Set user ID
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now())); // Set current timestamp
            stmt.setString(3, profile.getAddress()); // Set address
            stmt.setString(4, profile.getCity()); // Set city
            stmt.setString(5, profile.getState()); // Set state
            stmt.setString(6, profile.getZip()); // Set zip
            stmt.setBigDecimal(7, cart.getTotal()); // Set shipping amount

            // Execute the query
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }

            // Retrieve the generated keys
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1)); // Set the generated  ID
                } else {
                    throw new SQLException("Insert failed, no ID obtained.");
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Error inserting order", e);
        }
        return order;
    }

    @Override
    public OrderLineItem creatOrderLineItem(Order order, OrderLineItem line, Product product, ShoppingCartItem item) {

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.insertOrderItemLine(), PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Set the parameters for the order statement
            stmt.setInt(1, order.getOrderId()); // Set order ID
            stmt.setInt(2, product.getProductId()); // Set product ID
            stmt.setBigDecimal(3, product.getPrice()); // Set price
            stmt.setInt(4, item.getQuantity()); // Set quantity
            stmt.setBigDecimal(5, item.getDiscountPercent()); // Set discount

            // Execute the query
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }

            // Retrieve the generated keys
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    line.setOrderId(generatedKeys.getInt(1)); // Set the generated  ID
                } else {
                    throw new SQLException("Insert failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting order line item", e);
        }

        return line;
    }

}




