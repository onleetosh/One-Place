package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
     * Endpoint to process checkout: Converts the user's shopping cart into an order and clears the cart.
     *
     * @param principal The currently logged-in user's information.
     * @return The created Order object.
     */
    @PostMapping
    public Order checkout(Principal principal) {
        try {
            // Retrieve the currently logged-in user
            String username = principal.getName();
            User user = getCurrentUser(username);
            // Get the user's shopping cart
            ShoppingCart cart = getCartFromUser(user);
            // Get user profile (for address details)
            Profile profile = getUserProfile(user);
            // create the order to insert
            Order order = saveOrder(user, profile, cart);
            // insert the order line item
            saveOrderLineItems(order, cart);
            // clear the shopping cart after the order is confirmed
            shoppingCartDao.delete(user.getId());
            // return the created order
            return order;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing checkout");
        }
    }


    /**
     * Retrieve the currently logged-in user from the database.
     *
     * @param username The username of the logged-in user.
     * @return The User object.
     * @throws ResponseStatusException if the user is not found.
     */
    private User getCurrentUser(String username){
        User user = userDao.getByUserName(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    /**
     * Retrieve the user's shopping cart from the database.
     *
     * @param user The logged-in User object.
     * @return The ShoppingCart object containing items the user wants to purchase.
     * @throws ResponseStatusException if the shopping cart is empty or not found.
     */
    private ShoppingCart getCartFromUser(User user){
        ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());
        if (cart == null || cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shopping cart is empty");
        }
        return cart;
    }
    /**
     * Retrieve the user's profile from the database to fetch address details.
     *
     * @param user The logged-in User object.
     * @return The Profile object containing user address and contact information.
     * @throws ResponseStatusException if the profile is not found.
     */
    private Profile getUserProfile(User user){
        Profile profile = profileDao.getProfileById(user.getId());
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User profile not found");
        }
        return profile;
    }

    /**
     * Create and save a new order for the user.
     *
     * @param user    The logged-in User object.
     * @param profile The user's Profile object for address details.
     * @param cart    The ShoppingCart object containing items the user wants to purchase.
     * @return The created Order object.
     */
    private Order saveOrder(User user, Profile profile, ShoppingCart cart){
        // Create a new Order object and
        Order order = new Order();
        order.setUserId(user.getId());
        order.setDate(LocalDateTime.now());
        order.setShipping_amount(cart.getTotal());
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        return orderDao.createOrder(order, profile, cart);
    }

    /**
     * Create and save order line items for each product in the shopping cart.
     *
     * @param order The created Order object.
     * @param cart  The ShoppingCart object containing items the user wants to purchase.
     */
    private void saveOrderLineItems(Order order, ShoppingCart cart){
        // Loop through the cart and insert order line items for each item in the cart
        for (ShoppingCartItem cartItem : cart.getItems().values()) {
            Product product = cartItem.getProduct();
            // Create a new OrderLineItem object for each product
            OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setOrderId(order.getOrderId());
            orderLineItem.setProductId(product.getProductId());
            orderLineItem.setSalesPrice(product.getPrice().precision());
            orderLineItem.setQuantity(cartItem.getQuantity());
            orderLineItem.setDiscount(cartItem.getDiscountPercent().precision());
            orderDao.creatOrderLineItem(order, orderLineItem, product, cartItem);
        }
    }
}

