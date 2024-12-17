package org.yearup.data.interfaces;

import org.yearup.models.cart.ShoppingCart;

public interface ShoppingCartDao
{
    //CART
    ShoppingCart getByUserId(int userId);
    // add additional method signatures

    void doPost(int userId, int productId); // Add product to cart

    //rename
    void doPut(int userId, int productId, int quantity); // Update quantity of a product

    //rename
    void doDelete(int userId); // Clear all products from the user's cart

}


