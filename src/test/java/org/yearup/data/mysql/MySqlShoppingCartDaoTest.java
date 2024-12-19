package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yearup.models.cart.ShoppingCart;
import org.yearup.models.cart.ShoppingCartItem;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlShoppingCartDaoTest extends BaseDaoTestClass {
    private MySqlShoppingCartDao dao;

    @BeforeEach
    public void setup()
    {
        dao = new MySqlShoppingCartDao(dataSource);
    }


    @Test
    public void test_case_GetByUserId_whenNoItemsInCart() {
        // Arrange
        int userId = 1;

        // Act
        ShoppingCart result = dao.getByUserId(userId);  // Call the method

        // Assert
        assertNotNull(result);  // Ensure the result is not null
        assertTrue(result.getItems().isEmpty(), "The shopping cart should be empty when no items exist for the user");
    }


    //TODO: perform a second unit test for when items are in cart


    @Test
    public void test_case_add_to_ShoppingCart(){

        // Arrange
        int userId = 1;
        int productId = 1;

        // Act
        dao.post(userId, productId);  // Call the method

        // Assert
        ShoppingCart cart = dao.getByUserId(userId);  // Retrieve the shopping cart
        assertNotNull(cart);  // The shopping cart should exist
        assertEquals(1, cart.getItems().size(), "The cart should have exactly 1 item");

        // Verify that the item has been added to the cart
        ShoppingCartItem item = cart.get(productId);
        assertNotNull(item, "The product should have been added to the cart");
        assertEquals(1, item.getQuantity(), "The quantity should be 1 for a newly added product");
    }

    @Test
    public void test_case_update_Item_Quantity() {

        // arrange
        int userId = 1;
        int productId = 1;
        int updatedQuantity = 5;

        // act
        dao.post(userId, productId);        // add the item to the cart
        dao.update(userId, productId, updatedQuantity); // call the method

        // assert
        ShoppingCart cart = dao.getByUserId(userId); // retrieve the shopping cart
        assertNotNull(cart, "The shopping cart should exist");
        ShoppingCartItem item = cart.get(productId);
        assertNotNull(item, "The product should exist in the cart");
        assertEquals(updatedQuantity, item.getQuantity(), "The quantity should be updated to the new value");
    }

    @Test
    public void test_case_empty_ShoppingCart() {
        // arrange
        int userId = 1;
        int productId = 1;

        // act
        dao.post(userId, productId); // add the item to the cart
        dao.delete(userId); // delete the item from the cart

        // assert
        ShoppingCart cart = dao.getByUserId(userId); // retrieve the shopping cart
        assertNotNull(cart, "The shopping cart should exist");
        ShoppingCartItem item = cart.get(productId);
        assertNull(item, "The product should no longer exist in the cart after deletion");
        assertTrue(cart.getItems().isEmpty(), "The shopping cart should be empty after deleting the only item");
    }



}
