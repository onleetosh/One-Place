package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.interfaces.*;
import org.yearup.models.*;
import org.yearup.models.cart.*;
import org.yearup.models.order.*;

import java.security.Principal;
import java.util.Map;

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
    public ResponseEntity<?> checkout(Principal principal) {
        try {
            // Retrieve the currently logged-in user
            String username = principal.getName();
            User user = userDao.getCurrentUser(username);

            // Get the user's shopping cart
            ShoppingCart cart = shoppingCartDao.getConfirmedCart(user);

            // Get user profile (for address details)
            Profile profile = profileDao.getUserProfile(user);

            // Create and save the order
            Order order = orderDao.insertOrder(user, profile, cart);

            // Save order line items
            orderDao.insertOrderLineItem(order, cart);

            // Clear the shopping cart after the order is confirmed
            shoppingCartDao.delete(user);

            // Return the created order as a response
            return ResponseEntity.status(HttpStatus.CREATED).body(order);

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatus()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing checkout"));
        }
    }

}

