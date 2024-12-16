package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("cart")
@PreAuthorize("isAuthenticated()")
@CrossOrigin
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;


    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao,
                                  UserDao userDao,
                                  ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }
    /**
     * Get the shopping cart for the currently logged-in user. (JSON pass)
     */
    @GetMapping
    // each method in this controller requires a Principal object as a parameter
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
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

            ShoppingCart cart = shoppingCartDao.getByUserId(userId);
//debug statement
            if (cart.getItems().isEmpty()) {
                System.out.println("No items found in the shopping cart for user ID: " + userId);
            }
            return cart;
            // use the shoppingcartDao to get all items in the cart and return the cart
            //return shoppingCartDao.getByUserId(userId);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }



    /**
     * Add a product to the cart for the currently logged-in user
     */
    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    @PostMapping("/products/{id}")
    public void postCart(@PathVariable int id, Principal principal) {
        try {
            String userName = principal.getName();
            System.out.println("Authenticated user: " + userName);  // Log user

            User user = userDao.getByUserName(userName);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            System.out.println("User ID: " + user.getId() + ", Product ID: " + id);

            shoppingCartDao.addToCart(user.getId(), id);  // Call to add product to cart
            System.out.println("Product added to cart.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error...");
        }
    }



    /**
     * Update the quantity of a product in the cart for the currently logged-in user.
     */
    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated

    @PutMapping("/products/{id}")
    public void putCart(@PathVariable int id, @RequestBody ShoppingCartItem item, Principal principal){
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            shoppingCartDao.updateCart(user.getId(), id, item.getQuantity());
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error...");
        }
    }


    /**
     * Clear all products from the cart for the currently logged-in user.
     */
    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    @DeleteMapping()
    public void deleteCart(Principal principal){
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            shoppingCartDao.emptyCart(user.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error clearing cart");
        }
    }
}
