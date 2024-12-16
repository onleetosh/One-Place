package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    void addToCart(int userId, int productId); //add product to cart
    void updateCart(int userId, int productId, int quantity);
    void emptyCart(int userId);

}
