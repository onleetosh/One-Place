package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yearup.models.Product;
import org.yearup.models.Profile;
import org.yearup.models.cart.ShoppingCart;
import org.yearup.models.cart.ShoppingCartItem;
import org.yearup.models.order.Order;
import org.yearup.models.order.OrderLineItem;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlOrderDaoTest extends BaseDaoTestClass{
    private MySqlOrderDao dao;

    @BeforeEach
    public void setup()
    {
        dao = new MySqlOrderDao(dataSource);
    }


    @Test
    public void test_case_create_and_save_Order(){
        // Arrange
        Profile profile = new Profile();
        profile.setAddress("123 Main St");
        profile.setCity("TestCity");
        profile.setState("TestState");
        profile.setZip("12345");

        ShoppingCart cart = new ShoppingCart();
        cart.setTotal(new BigDecimal("99.99"));

        Order order = new Order();
        order.setUserId(1); // Assume a valid user ID

        // Act
        Order savedOrder = dao.createOrder(order, profile, cart);

        // Assert
        assertNotNull(savedOrder, "Saved order should not be null.");
        assertTrue(savedOrder.getOrderId() > 0, "Order ID should be greater than 0.");
        assertEquals(order.getUserId(), savedOrder.getUserId(), "User ID should match.");

    }

    @Test
    public void test_case_create_and_save_OrderLineItem() {
        // Arrange

        // Act

        // Assert
    }

}
