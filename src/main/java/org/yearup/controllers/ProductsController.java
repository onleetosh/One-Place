package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Product;
import org.yearup.data.interfaces.ProductDao;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("products")
@CrossOrigin
public class ProductsController
{
    private ProductDao productDao;


    @Autowired
    public ProductsController(ProductDao productDao)
    {
        this.productDao = productDao;
    }


    /**
     * Endpoint to search for products based on optional query parameters such as
     * category, price range, and color.
     * This endpoint is accessible to all users.
     *
     * @param categoryId Optional category ID to filter products.
     * @param minPrice Optional minimum price to filter products.
     * @param maxPrice Optional maximum price to filter products.
     * @param color Optional color to filter products.
     * @return List of products matching the search criteria.
     */
    @GetMapping()
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> search(
            @RequestParam(name = "cat", required = false) Integer categoryId,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "color", required = false) String color) {
        try {
            List<Product> products = productDao.search(categoryId, minPrice, maxPrice, color);
            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(products);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint to fetch a product by its ID.
     * This endpoint is accessible to all users.
     *
     * @param id ID of the product to fetch.
     * @return The product with the specified ID.
     * @throws ResponseStatusException if the product is not found or an error occurs.
     */
    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Product> getById(@PathVariable int id) {
        try {
            Product product = productDao.getById(id);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(product);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * Endpoint to add a new product. Only accessible to users with ADMIN role.
     *
     * @param product The product object to add.
     * @return The newly created product.
     * @throws ResponseStatusException if an error occurs during creation.
     */
    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productDao.create(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * Endpoint to update an existing product by its ID. Only accessible to users with ADMIN role.
     *
     * @param id The ID of the product to update.
     * @param product The updated product data.
     * @throws ResponseStatusException if an error occurs during the update.
     */
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateProduct(@PathVariable int id, @RequestBody Product product) {
        try {
            productDao.update(id, product);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    /**
     * Endpoint to delete a product by its ID. Only accessible to users with ADMIN role.
     *
     * @param id The ID of the product to delete.
     * @throws ResponseStatusException if the product is not found or an error occurs during deletion.
     */

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        try {
            Product product = productDao.getById(id);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            productDao.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
