package org.yearup.data.interfaces;

import org.yearup.models.Product;
import org.yearup.models.Profile;
import org.yearup.models.User;
import org.yearup.models.cart.ShoppingCart;
import org.yearup.models.cart.ShoppingCartItem;
import org.yearup.models.order.Order;
import org.yearup.models.order.OrderLineItem;

public interface OrderDao {

    /**
     * Create a new order in the database.
     *
     * @param order   The Order object containing details such as user ID, date, shipping amount, and address information.
     * @param profile The Profile object containing user-specific details like address, city, state, and zip code.
     * @param cart    The ShoppingCart object containing the items to be included in the order.
     * @return The created Order object with its generated ID.
     */
    Order createOrder(Order order,Profile profile, ShoppingCart cart);

    /**
     * Create a new order line item in the database.
     *
     * @param order   The Order object to which this line item belongs.
     * @param line    The OrderLineItem object containing details such as product ID, sales price, quantity, and discount.
     * @param product The Product object representing the item being purchased.
     * @param item    The ShoppingCartItem object representing the product and quantity in the user's cart.
     * @return The created OrderLineItem object with its details saved in the database.
     */
    OrderLineItem creatOrderLineItem(Order order, OrderLineItem line, Product product, ShoppingCartItem item);

    /**
     * Create and insert a new order for the user.
     *
     * @param user    The logged-in User object.
     * @param profile The user's Profile object for address details.
     * @param cart    The ShoppingCart object containing items the user wants to purchase.
     * @return The created Order object.
     */
    Order insertOrder(User user, Profile profile, ShoppingCart cart);

    /**
     * Create and insert order line items for each product in the shopping cart.
     *
     * @param order The created Order object.
     * @param cart  The ShoppingCart object containing items the user wants to purchase.
     */

    void insertOrderLineItem(Order order, ShoppingCart cart);

}
