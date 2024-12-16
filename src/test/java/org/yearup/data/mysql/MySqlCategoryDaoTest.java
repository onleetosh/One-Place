package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yearup.models.Category;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlCategoryDaoTest extends BaseDaoTestClass {

    private MySqlCategoryDao dao;

    @BeforeEach
    public void setup()
    {
        dao = new MySqlCategoryDao(dataSource);
    }


    @Test
    public void getAllCategories_test(){

        //arrange
        List<Category> categories = List.of(
                new Category(1, "Electronics", "Explore the latest gadgets and electronic devices."),
                new Category(2, "Fashion", "Discover trendy clothing and accessories for men and women."),
                new Category(3, "Home & Kitchen", "Find everything you need to decorate and equip your home.")
        );
        //act
        List<Category> getCategories = dao.getAllCategories();

        // Assert: Verify that the retrieved categories match the expected ones
        assertNotNull(getCategories, "The category list should not be null");
        assertEquals(categories.size(), getCategories.size(), "The number of categories should match");

        for (int i = 0; i < categories.size(); i++) {
            assertEquals(categories.get(i).getCategoryId(), getCategories.get(i).getCategoryId(), "Category IDs should match");
            assertEquals(categories.get(i).getName(), getCategories.get(i).getName(), "Category names should match");
            assertEquals(categories.get(i).getDescription(), getCategories.get(i).getDescription(), "Category descriptions should match");
        }
    }


    @Test
    public void getCategoryById_test(){
        //arrange
        int categoryId = 1;
        Category category =   new Category(1, "Electronics", "Explore the latest gadgets and electronic devices.");

        //act
        Category getCategories = dao.getById(categoryId);
        //assert
        //TODO : change  the message
        assertNotNull(getCategories, "The product should not be null");
        assertEquals(category.getCategoryId(), getCategories.getCategoryId(), "The product retrieved should match the expected product");
    }


    @Test
    public void test_MySqlCategoryDao_create(){

        // Arrange: Define the new category to be created
        Category newCategory = new Category();
        newCategory.setName("Sports & Outdoors");
        newCategory.setDescription("Gear up for your outdoor adventures");

        // Act: Call the create method to insert the new category into the database
        Category createdCategory = dao.create(newCategory);

        // Assert: Verify that the created category has a valid ID and matches the input values
        assertNotNull(createdCategory, "The created category should not be null.");
        assertNotNull(createdCategory.getCategoryId(), "The created category ID should not be null.");
        assertTrue(createdCategory.getCategoryId() > 0, "The created category ID should be greater than 0.");
        assertEquals(newCategory.getName(), createdCategory.getName(), "The category name should match.");
        assertEquals(newCategory.getDescription(), createdCategory.getDescription(), "The category description should match.");

        // Additionally, retrieve the category from the database and verify
        Category categoryFromDb = dao.getById(createdCategory.getCategoryId());
        assertNotNull(categoryFromDb, "The category should be retrievable from the database.");
        assertEquals(createdCategory.getCategoryId(), categoryFromDb.getCategoryId(), "The category ID should match.");
        assertEquals(createdCategory.getName(), categoryFromDb.getName(), "The category name should match.");
        assertEquals(createdCategory.getDescription(), categoryFromDb.getDescription(), "The category description should match.");

    }


    @Test
    public void testUpdateCategory() {
        // Arrange: Create a category and insert it into the database
        Category originalCategory = new Category();
        originalCategory.setName("Books");
        originalCategory.setDescription("A wide variety of books and literature.");

        // Insert the category to get a valid ID
        Category insertedCategory = dao.create(originalCategory);

        // Modify the category for the update
        Category updatedCategory = new Category();
        updatedCategory.setName("Books & Magazines");
        updatedCategory.setDescription("Books, magazines, and other reading materials.");

        // Act: Update the category in the database
        dao.update(insertedCategory.getCategoryId(), updatedCategory);

        // Assert: Retrieve the category from the database and verify the updates
        Category retrievedCategory = dao.getById(insertedCategory.getCategoryId());

        assertNotNull(retrievedCategory, "The retrieved category should not be null.");
        assertEquals(insertedCategory.getCategoryId(), retrievedCategory.getCategoryId(), "The category ID should not change.");
        assertEquals(updatedCategory.getName(), retrievedCategory.getName(), "The updated name should match.");
        assertEquals(updatedCategory.getDescription(), retrievedCategory.getDescription(), "The updated description should match.");
    }


    @Test
    public void test_delete() {
        // Arrange: Insert a product into the database for deletion
        Category category = new Category(
                0,  // Assuming the product ID is auto-generated
                "Test Category",
                "A test product for deletion."
        );

        // Create the product and retrieve the generated ID
        Category testCategory = dao.create(category);
        int categoryIdToDelete = testCategory.getCategoryId();

        // Act: Delete the product
        dao.delete(categoryIdToDelete);

        // Assert: Verify the product is deleted
        Category result = dao.getById(categoryIdToDelete);
        assertNull(result, "The Category should no longer exist in the database after deletion.");
    }
}
