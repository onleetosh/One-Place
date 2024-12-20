/**
 * This interface defines the contract for operations related to the shopping cart functionality.
 * It provides methods for adding, updating, retrieving, and clearing products in a user's shopping cart.
 * Implementations of this interface interact with the underlying database or data storage to persist
 * and manage the shopping cart data for users in an e-commerce system.
 */
package org.yearup.data.interfaces;

import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.User;
import org.yearup.models.cart.ShoppingCart;


public interface ShoppingCartDao {



    /**
     * Retrieve the shopping cart for a specific user by their ID.
     *
     * @param user The ID of the user whose shopping cart is to be retrieved.
     * @return The ShoppingCart object for the specified user, or null if no cart is found.
     */
    ShoppingCart getByUserId(User user);


    /**
     * Retrieve the user's shopping cart from the database.
     *
     * @param user The logged-in User object.
     * @return The ShoppingCart object containing items the user wants to purchase.
     * @throws ResponseStatusException if the shopping cart is empty or not found.
     */
    ShoppingCart getConfirmedCart(User user);
    /**
     * Add a product to the shopping cart for a specific user.
     *
     * @param user The ID of the user whose cart the product is to be added to.
     * @param productId The ID of the product to add to the cart.
     */
    void post(User user, int productId); // Add product to cart

    /**
     * Update the quantity of a product in the user's shopping cart.
     *
     * @param user The ID of the user whose cart is being updated.
     * @param productId The ID of the product whose quantity is to be updated.
     * @param quantity The new quantity for the product in the cart.
     */
    void update(User user, int productId, int quantity); // Update quantity of a product

    /**
     * Clear all products from the shopping cart of a specific user.
     *
     * @param userId The ID of the user whose cart is to be cleared.
     */
    void delete(User userId); // Clear all products from the user's cart

}


