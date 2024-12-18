/**
 * This interface defines the contract for managing product-related operations.
 * It provides methods for searching, retrieving, creating, updating, and deleting products.
 * Implementations of this interface interact with the underlying data storage to manage
 * product data, such as name, price, category, stock, and other product details.
 */
package org.yearup.data.interfaces;

import org.yearup.models.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductDao {

    /**
     * Search for products based on various filter criteria.
     *
     * @param categoryId The ID of the category to filter by, or null for no category filter.
     * @param minPrice   The minimum price of products to filter by, or null for no price filter.
     * @param maxPrice   The maximum price of products to filter by, or null for no price filter.
     * @param color      The color of products to filter by, or null for no color filter.
     * @return A list of products that match the given criteria.
     */
    List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color);

    /**
     * List all products that belong to a specific category.
     *
     * @param categoryId The ID of the category to filter by.
     * @return A list of products that belong to the given category.
     */
    List<Product> listByCategoryId(int categoryId);

    /**
     * Retrieve a product by its unique ID.
     *
     * @param productId The ID of the product to retrieve.
     * @return The Product object with the specified ID, or null if no product is found.
     */
    Product getById(int productId);

    /**
     * Create a new product and insert it into the database.
     *
     * @param product The Product object containing the details of the new product.
     * @return The created Product object, potentially with a generated ID.
     */
    Product create(Product product);

    /**
     * Update the details of an existing product.
     *
     * @param productId The ID of the product to update.
     * @param product   The Product object containing the updated product details.
     */
    void update(int productId, Product product);

    /**
     * Delete a product from the database by its ID.
     *
     * @param productId The ID of the product to delete.
     */
    void delete(int productId);
}
