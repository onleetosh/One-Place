package org.yearup.data.interfaces;

import org.yearup.models.Product;
import org.yearup.models.Profile;
import org.yearup.models.User;
import org.yearup.models.cart.ShoppingCart;
import org.yearup.models.cart.ShoppingCartItem;
import org.yearup.models.order.Order;
import org.yearup.models.order.OrderLineItem;

public interface OrderDao {

    Order postOrder(Order order,Profile profile, ShoppingCart cart);

    OrderLineItem creatOrderLineItem(Order order, OrderLineItem line, Product product, ShoppingCartItem item);



}
