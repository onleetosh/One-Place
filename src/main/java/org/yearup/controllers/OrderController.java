package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.interfaces.OrderDao;
import org.yearup.data.interfaces.ProfileDao;
import org.yearup.data.interfaces.ShoppingCartDao;
import org.yearup.data.interfaces.UserDao;
import org.yearup.models.Product;
import org.yearup.models.Profile;
import org.yearup.models.User;
import org.yearup.models.cart.ShoppingCart;
import org.yearup.models.cart.ShoppingCartItem;
import org.yearup.models.order.Order;
import org.yearup.models.order.OrderLineItem;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("orders")
public class OrderController {

    private final OrderDao orderDao;
    private final UserDao userDao;
    private final ShoppingCartDao shoppingCartDao;
    private final ProfileDao profileDao;

    @Autowired
    public OrderController(OrderDao orderDao, UserDao userDao, ShoppingCartDao shoppingCartDao, ProfileDao profileDao) {
        this.orderDao = orderDao;
        this.userDao = userDao;
        this.shoppingCartDao = shoppingCartDao;
        this.profileDao = profileDao;
    }

    /**
     * Process checkout: Convert shopping cart to an order and clear the cart.
     */
    @PostMapping
    public Order checkout(Principal principal) {
        try {
            // retrieve the currently logged-in user
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }

            // get the user's shopping cart
            ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());
            if (cart == null || cart.getItems().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shopping cart is empty");
            }

            // get user profile (for address details)
            Profile profile = profileDao.getProfileById(user.getId());
            if (profile == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User profile not found");
            }

            // create the order based on the shopping cart and user profile
            Order order = new Order();
            order.setUserId(user.getId());
            order.setDate(LocalDateTime.now());
            order.setShipping_amount(cart.getTotal());
            order.setAddress(profile.getAddress());
            order.setCity(profile.getCity());
            order.setState(profile.getState());
            order.setZip(profile.getZip());

            // insert the order into the database
            Order createdOrder = orderDao.postOrder(order, profile, cart);

            // insert order line items for each item in the cart
            for (ShoppingCartItem cartItem : cart.getItems().values()) {
                Product product = cartItem.getProduct(); // Get the product associated with the cart item
                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setOrderId(createdOrder.getOrderId());
                orderLineItem.setProductId(product.getProductId());
                orderLineItem.setSalesPrice(product.getPrice().precision());
                orderLineItem.setQuantity(cartItem.getQuantity());
                orderLineItem.setDiscount(cartItem.getDiscountPercent().precision());

                // insert each order line item into the database
                orderDao.creatOrderLineItem(createdOrder, orderLineItem, product, cartItem);
            }

            // clear the shopping cart after the order is confirmed
            shoppingCartDao.doDelete(user.getId());

            // return the created order object directly
            return createdOrder;

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing checkout");
        }
    }


}

