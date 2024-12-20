/**
 * This interface defines the contract for managing category-related operations.
 * It provides methods for retrieving, creating, updating, and deleting categories.
 * Implementations of this interface interact with the underlying data storage to manage
 * category data, such as category name, description, and associated products.
 */

package org.yearup.data.interfaces;

import org.yearup.models.Category;

import java.util.List;

public interface CategoryDao {
    /**
     * Retrieve all categories from the database.
     *
     * @return A list of all categories.
     */
    List<Category> getAllCategories();

    /**
     * Retrieve a category by its unique ID.
     *
     * @param categoryId The ID of the category to retrieve.
     * @return The Category object if found, or null if no category exists with the given ID.
     */
    Category getById(int categoryId);

    /**
     * Retrieve a category by its unique name.
     *
     * @param name The name of the category to retrieve.
     * @return The Category object if found, or null if no category exists with the given name.
     */
    Category getByName(String name);

    /**
     * Create a new category in the database.
     *
     * @param category The Category object containing the details of the category to create.
     * @return The newly created Category object with its generated ID.
     */
    Category create(Category category);

    /**
     * Update an existing category in the database.
     *
     * @param categoryId The ID of the category to update.
     * @param category   The updated Category object containing new details.
     */
    void update(int categoryId, Category category);

    /**
     * Delete a category from the database by its unique ID.
     *
     * @param categoryId The ID of the category to delete.
     */
    void delete(int categoryId);

    boolean categoryExist(String name);


}
