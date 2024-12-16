package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

import static com.mysql.cj.conf.PropertyKey.logger;

// add the annotations to make this a REST controller
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
// add annotation to allow cross site origin requests

@RestController
@RequestMapping("categories")
@CrossOrigin
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;


    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao){
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    /**
     * Get all categories.
     */
    @GetMapping
    // add the appropriate annotation for a get action
    public List<Category> getAll()
    {

        // find and return all categories
        try
        {
            return categoryDao.getAllCategories();
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Oops... our bad.");
        }
    }

    /**
     * Get category by ID.
     */
    // add the appropriate annotation for a get action
    @GetMapping("{id}")
    public Category getById(@PathVariable int id)
    {
        // get the category by id
        Category category = categoryDao.getById(id);
        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        return category;
    }

    /**
     * Get all products within a specific category.
     */
    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        //check is category exist first

        // get a list of product by categoryId
        try
        {
            return productDao.listByCategoryId(categoryId);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Oops... our bad.");
        }
    }

    /**
     * Add a new category.
     * Accessible only to users with ADMIN role.
     */
    // add annotation to call this method for a POST action
    // add annotation to ensure that only an ADMIN can call this function
    public Category addCategory(@RequestBody Category category)
    {
        // Check if the category already exists
        if (categoryExists(category.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists.");
        }

        try
        {
            return categoryDao.createCategory(category);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    /**
     * Helper method to check if a category exists.
     */
    private boolean categoryExists(String name) {
        return categoryDao.getByName(name) != null;
    }




    /**
     * Update an existing category.
     * Accessible only to users with ADMIN role.
     */
    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        // update the category by id
        try
        {
            categoryDao.update(id, category);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error...");
        }     // update the category by id
        try
        {
            categoryDao.update(id, category);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error...");
        }
    }

    /**
     * Delete a category by ID.
     * Accessible only to users with ADMIN role.
     */
    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping({"id"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable int id)
    {
        // delete the category by id
        try
        {
            categoryDao.delete(id);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error...");
        }
    }
}
