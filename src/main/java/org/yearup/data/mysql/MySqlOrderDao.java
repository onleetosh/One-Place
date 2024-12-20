package org.yearup.data.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yearup.data.interfaces.OrderDao;
import org.yearup.models.Product;
import org.yearup.models.Profile;
import org.yearup.models.User;
import org.yearup.models.cart.ShoppingCart;
import org.yearup.models.cart.ShoppingCartItem;
import org.yearup.models.order.Order;
import org.yearup.models.order.OrderLineItem;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data Access Object (DAO) for managing Order-related operations in a MySQL database.
 * This class provides CRUD operations for orders and their associated line items.
 */
@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    private static final Logger logger = LoggerFactory.getLogger(MySqlOrderDao.class);

    /**
     * Constructor for MySqlOrderDao.
     *
     * @param dataSource The DataSource used to obtain database connections.
     */
    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }


    /**
     * Creates a new order in the database.
     * This method inserts an order record and assigns a generated order ID.
     *
     * @param order    The Order object containing the order data to be created.
     * @param profile  The Profile object containing user address details.
     * @param cart     The ShoppingCart object containing the total price.
     * @return The created Order object, including the generated order ID.
     */
    @Override
    public Order createOrder(Order order,
                             Profile profile,
                             ShoppingCart cart) {
        // Establish a connection to the database, prepare the SQL statement and generate key
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.insertOrders(),
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Set the parameters to process query insert
            stmt.setInt(1, order.getUserId()); // Set user ID
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now())); // Set current timestamp
            stmt.setString(3, profile.getAddress()); // Set address
            stmt.setString(4, profile.getCity()); // Set city
            stmt.setString(5, profile.getState()); // Set state
            stmt.setString(6, profile.getZip()); // Set zip
            stmt.setBigDecimal(7, cart.getTotal()); // Set shipping_amount (order total)
            // Execute the query
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            // Retrieve the generated ID and set the orderId
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1));
                    logger.debug("Created order with ID: {}", order.getOrderId());

                } else {
                    // Handle any SQL exceptions that occur
                    throw new SQLException("Insert failed, no ID obtained.");
                }
            }
        }
        catch (SQLException e) {
            logger.error("Error inserting order", e);
            // Handle any SQL exceptions that occur
            throw new RuntimeException("Error inserting order", e);
        }
        return order; // Return the created order with the generated order ID
    }

    /**
     * Creates a new order line item in the database.
     * This method inserts an order line item and assigns a generated ID.
     *
     * @param order    The Order object associated with the order line for an item.
     * @param line     The OrderLineItem object containing the order line for an item details.
     * @param product  The Product object containing product details for the order line for an item .
     * @param item     The ShoppingCartItem object containing item-specific details (e.g., quantity, discount).
     * @return The created OrderLineItem object, including the generated line item ID.
     */
    @Override
    public OrderLineItem creatOrderLineItem(Order order,
                                            OrderLineItem line,
                                            Product product,
                                            ShoppingCartItem item) {
        // Establish a connection to the database, prepare the SQL statement and generate key
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.insertOrderLineItems(),
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Set the parameters to process query insert
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
            // Retrieve the generated ID and set the order_line_item_id
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    line.setOrderLineId(generatedKeys.getInt(1)); // Set the generated  ID
                    logger.debug("Created order line item with ID: {}", line.getOrderLineId());
                } else {
                    // Handle any SQL exceptions that occur
                    throw new SQLException("Insert failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error inserting order line item", e);
            // Handle any SQL exceptions that occur
            throw new RuntimeException("Error inserting order line item", e);
        }
        return line; // Return the created order line item with the generated ID
    }

    @Override
    public Order insertOrder(User user, Profile profile, ShoppingCart cart) {
        // Create a new Order object and
        Order order = new Order();
        order.setUserId(user.getId());
        order.setDate(LocalDateTime.now());
        order.setShipping_amount(cart.getTotal());
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        return createOrder(order, profile, cart);
    }

    /**
     * Create and save order line items for each product in the shopping cart.
     *
     * @param order The created Order object.
     * @param cart  The ShoppingCart object containing items the user wants to purchase.
     */
    @Override
    public void insertOrderLineItem(Order order, ShoppingCart cart) {
        // Loop through the cart and insert order line items for each item in the cart
        for (ShoppingCartItem cartItem : cart.getItems().values()) {
            Product product = cartItem.getProduct();
            // Create a new OrderLineItem object for each product
            OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setOrderId(order.getOrderId());
            orderLineItem.setProductId(product.getProductId());
            orderLineItem.setSalesPrice(product.getPrice());
            orderLineItem.setQuantity(cartItem.getQuantity());
            orderLineItem.setDiscount(cartItem.getDiscountPercent().precision());
            creatOrderLineItem(order, orderLineItem, product, cartItem);
        }
    }
}




