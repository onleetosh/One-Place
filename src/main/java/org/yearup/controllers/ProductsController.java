package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public List<Product> search(@RequestParam(name="cat", required = false) Integer categoryId,
                                @RequestParam(name="minPrice", required = false) BigDecimal minPrice,
                                @RequestParam(name="maxPrice", required = false) BigDecimal maxPrice,
                                @RequestParam(name="color", required = false) String color
                                )
    {
        try
        {
            return productDao.search(categoryId, minPrice, maxPrice, color);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error...");
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
    public Product getById(@PathVariable int id )
    {
        try
        {
            var product = productDao.getById(id);

            if(product == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            return product;
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error...");
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
    public Product addProduct(@RequestBody Product product)
    {
        try
        {
            return productDao.create(product);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error...");
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
    public void updateProduct(@PathVariable int id, @RequestBody Product product)
    {
        try
        {
            productDao.update(id, product);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error...");
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
    public void deleteProduct(@PathVariable int id)
    {
        try
        {
            var product = productDao.getById(id);

            if(product == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            productDao.delete(id);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error...");
        }
    }
}
