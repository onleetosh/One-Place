package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.interfaces.ProductDao;
import org.yearup.data.interfaces.ShoppingCartDao;
import org.yearup.data.interfaces.UserDao;
import org.yearup.models.cart.ShoppingCart;
import org.yearup.models.cart.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("cart")
@PreAuthorize("isAuthenticated()")
@CrossOrigin
public class ShoppingCartController {
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao,
                                  UserDao userDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    /**
     * Get the shopping cart for the currently logged-in user. (JSON pass)
     */
    @GetMapping
    // each method in this controller requires a Principal object as a parameter
    public ResponseEntity<ShoppingCart> getCart(Principal principal) {
        try {
            // get the currently logged in username
            String userName = principal.getName();

            // find database user by userId
            // Find the user by username
            User user = userDao.getByUserName(userName);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            //No items found in the shopping cart for user ID: 7  (because my account is new)
            int userId = user.getId();

            ShoppingCart cart = shoppingCartDao.getByUserId(user);
            //debug statement
            if (cart.getItems().isEmpty()) {
                System.out.println("No items found in the shopping cart for user ID: " + userId);
            }
            return ResponseEntity.ok(cart);
            // use the shoppingcartDao to get all items in the cart and return the cart
            //return shoppingCartDao.getByUserId(userId);
        }
        catch(Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Add a product to the cart for the currently logged-in user. (json passed )
     */
    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    @PostMapping("/products/{id}")
    public ResponseEntity<?> postCart(@PathVariable int id, Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            shoppingCartDao.post(user, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product added to cart");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }


    /**
     * Update the quantity of a product in the cart for the currently logged-in user. (json passed)
     */
    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("/products/{id}")
    public ResponseEntity<?> putCart(@PathVariable int id, @RequestBody ShoppingCartItem item, Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            shoppingCartDao.update(user, id, item.getQuantity());
            return ResponseEntity.ok("Product quantity updated");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }


    /**
     * Clear all products from the cart for the currently logged-in user.
     */
    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    @DeleteMapping()
    public ResponseEntity<?> deleteCart(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }
            shoppingCartDao.delete(user);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Shopping cart cleared successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while clearing the shopping cart.");
        }
    }

}
