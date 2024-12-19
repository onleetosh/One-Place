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
    public void test_case_get_all_categories(){

        // Arrange
        List<Category> categories = List.of(
                new Category(1, "Electronics", "Explore the latest gadgets and electronic devices."),
                new Category(2, "Fashion", "Discover trendy clothing and accessories for men and women."),
                new Category(3, "Home & Kitchen", "Find everything you need to decorate and equip your home.")
        );
        // Act
        List<Category> getCategories = dao.getAllCategories();

        // Assert
        assertNotNull(getCategories, "The category list should not be null");
        assertEquals(categories.size(), getCategories.size(), "The number of categories should match");

        for (int i = 0; i < categories.size(); i++) {
            assertEquals(categories.get(i).getCategoryId(), getCategories.get(i).getCategoryId(), "Category IDs should match");
            assertEquals(categories.get(i).getName(), getCategories.get(i).getName(), "Category names should match");
            assertEquals(categories.get(i).getDescription(), getCategories.get(i).getDescription(), "Category descriptions should match");
        }
    }



    @Test
    public void test_case_get_categories_by_id(){
        // Arrange
        int categoryId = 1;
        Category category = new Category(1, "Electronics", "Explore the latest gadgets and electronic devices.");

        // Act
        Category getCategories = dao.getById(categoryId);
        // Assert
        assertNotNull(getCategories, "The category should not be null");
        assertEquals(category.getCategoryId(), getCategories.getCategoryId(), "The category retrieved should match the expected product");
    }


    @Test
    public void test_MySqlCategoryDao_create(){

        // Arrange
        Category newCategory = new Category();
        newCategory.setName("Sports & Outdoors");
        newCategory.setDescription("Gear up for your outdoor adventures");

        // Act
        Category createdCategory = dao.create(newCategory);

        // Assert:
        assertNotNull(createdCategory, "The created category should not be null.");
        assertNotNull(createdCategory.getCategoryId(), "The created category ID should not be null.");
        assertTrue(createdCategory.getCategoryId() > 0, "The created category ID should be greater than 0.");
        assertEquals(newCategory.getName(), createdCategory.getName(), "The category name should match.");
        assertEquals(newCategory.getDescription(), createdCategory.getDescription(), "The category description should match.");
    }


    @Test
    public void test_case_update_categories() {
        // Arrange: Create a category and insert it into the database
        Category c1 = new Category();
        c1.setName("Books");
        c1.setDescription("A wide variety of books and literature.");

        // Insert the category to get a valid ID
        Category insertedC1 = dao.create(c1);

        Category updatedC1 = new Category();
        updatedC1.setName("Books & Magazines");
        updatedC1.setDescription("Books, magazines, and other reading materials.");

        // Act
        dao.update(insertedC1.getCategoryId(), updatedC1);

        // Assert
        Category getCategories = dao.getById(insertedC1.getCategoryId());

        assertNotNull(getCategories, "The retrieved category should not be null.");
        assertEquals(insertedC1.getCategoryId(), getCategories.getCategoryId(), "The category ID should not change.");
        assertEquals(updatedC1.getName(), getCategories.getName(), "The updated name should match.");
        assertEquals(updatedC1.getDescription(), getCategories.getDescription(), "The updated description should match.");
    }


    @Test
    public void test_case_delete() {
        // Arrange
        Category c1 = new Category(
                0,  // Assuming the product ID is auto-generated
                "Test Category",
                "A test product for deletion."
        );

        Category insertedC1 = dao.create(c1);
        int categoryIdToDelete = insertedC1.getCategoryId();

        // Act
        dao.delete(categoryIdToDelete);

        // Assert:
        Category result = dao.getById(categoryIdToDelete);
        assertNull(result, "The Category should no longer exist in the database after deletion.");
    }
}
