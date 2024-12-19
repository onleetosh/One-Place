package org.yearup.data.mysql;

import org.apache.ibatis.javassist.bytecode.DuplicateMemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yearup.models.Product;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MySqlProductDaoTest extends BaseDaoTestClass
{
    private MySqlProductDao dao;

    @BeforeEach
    public void setup()
    {
        dao = new MySqlProductDao(dataSource);
    }


    @Test
    public void getById_shouldReturn_theCorrectProduct()
    {
        // arrange
        int productId = 1;
        Product expected = new Product()
        {{
            setProductId(1);
            setName("Smartphone");
            setPrice(new BigDecimal("499.99"));
            setCategoryId(1);
            setDescription("A powerful and feature-rich smartphone for all your communication needs.");
            setColor("Black");
            setStock(50);
            setFeatured(false);
            setImageUrl("smartphone.jpg");
        }};

        // act
        var actual = dao.getById(productId);

        // assert
        assertEquals(expected.getPrice(), actual.getPrice(), "Because I tried to get product 1 from the database.");
    }


    @Test
    public void test_case_search_and_filter() {
        // Arrange
        List<Product> expectedProducts = List.of(
                new Product() {{
                    setProductId(1);
                    setName("Smartphone");
                    setPrice(new BigDecimal("499.99"));
                    setCategoryId(1);
                    setDescription("A powerful and feature-rich smartphone for all your communication needs.");
                    setColor("Black");
                    setStock(50);
                    setFeatured(false);
                    setImageUrl("smartphone.jpg");
                }},
                new Product() {{
                    setProductId(3);
                    setName("Headphones");
                    setPrice(new BigDecimal("99.99"));
                    setCategoryId(1);
                    setDescription("Immerse yourself in music with these high-quality headphones.");
                    setColor("White");
                    setStock(100);
                    setFeatured(true);
                    setImageUrl("headphones.jpg");
                }}
        );

        Integer categoryId = 1; // Electronics
        BigDecimal minPrice = new BigDecimal("50");
        BigDecimal maxPrice = new BigDecimal("500");
        String color = null; // No filter on color

        // Act
        List<Product> actualProducts = dao.search(categoryId, minPrice, maxPrice, color);

        // Assert
        assertEquals(expectedProducts.size(), actualProducts.size(), "Should return the correct number of products");
    }

    /**
     * test for  public List<Product> listByCategoryId(int categoryId)
     */
    @Test
    public void test_case_list_By_CategoryId() {
        // Arrange
        int categoryId = 1; // Electronics
        Product p1 = new Product() {{
            setProductId(1);
            setName("Smartphone");
            setPrice(new BigDecimal("499.99"));
            setCategoryId(1);
            setDescription("A powerful and feature-rich smartphone for all your communication needs.");
            setColor("Black");
            setStock(50);
            setFeatured(false);
            setImageUrl("smartphone.jpg");
        }};

        Product p2 = new Product() {{
            setProductId(2);
            setName("Laptop");
            setPrice(new BigDecimal("899.99"));
            setCategoryId(1);
            setDescription("A high-performance laptop for work and entertainment.");
            setColor("Gray");
            setStock(30);
            setFeatured(false);
            setImageUrl("laptop.jpg");
        }};

        Product p3 = new Product() {{
            setProductId(3);
            setName("Headphones");
            setPrice(new BigDecimal("99.99"));
            setCategoryId(1);
            setDescription("Immerse yourself in music with these high-quality headphones.");
            setColor("White");
            setStock(100);
            setFeatured(true);
            setImageUrl("headphones.jpg");
        }};
        List<Product> expectedProducts = List.of(p1, p2, p3);

        // Act
        List<Product> actualProducts = dao.listByCategoryId(categoryId);

        // Assert
        assertNotNull(actualProducts, "The product list should not be null");
        assertEquals(expectedProducts.size(), actualProducts.size(), "The number of products should match");
        assertFalse(actualProducts.containsAll(expectedProducts), "The actual product list should contain the expected products");
    }

    /**
     * test for public Product getById(int productId)
     */
    @Test
    public void test_case_get_product_by_id_(){
        // Arrange: Define the expected product
        int productId = 1;
        Product p2 = new Product(
                1,
                "Smartphone",
                new BigDecimal("499.99"),
                1,
                "A powerful and feature-rich smartphone for all your communication needs.",
                "Black",
                50,
                false,
                "smartphone.jpg" );

        // Act: Call the method to get the product by ID
        Product p1 = dao.getById(productId);

        // Assert: Verify that the returned product matches the expected product
        assertNotNull(p1, "The product should not be null");
        assertEquals(p2.getProductId(), p1.getProductId(), "The product retrieved should match the expected product");
    }



    /**
     * Test for public Product create(Product product)
     */
    @Test
    public void test_case_create_new_product(){
        // Arrange

        Product p1 = new Product(
                0,
                "Ipad",
                new BigDecimal("199.99"),
                3,
                "A powerful and feature-rich smartphone for all your communication needs.",
                "Blue",
                50,
                false,
                "iPad.jpg" );
        // Act
        Product p2 = dao.create(p1);



        // Assert
        assertNotNull(p2, "The created product should not be null");
        assertEquals(p1.getName(), p2.getName(), "The name should match");
        assertEquals(p1.getPrice(), p2.getPrice(), "The price should match");
        assertEquals(p1.getCategoryId(), p2.getCategoryId(), "The category ID should match");
        assertEquals(p1.getDescription(), p2.getDescription(), "The description should match");
        assertEquals(p1.getColor(), p2.getColor(), "The color should match");
        assertEquals(p1.getStock(), p2.getStock(), "The stock should match");
        assertEquals(p1.isFeatured(), p2.isFeatured(), "The featured flag should match");
        assertEquals(p1.getImageUrl(), p2.getImageUrl(), "The image URL should match");
    }

    @Test
    public void test_case_create_duplicate_product() {
        // Arrange
        Product p1 = new Product(
                999,
                "PS5 pro",
                new BigDecimal("499.99"),
                1,
                "A powerful and feature-rich smartphone for all your communication needs.",
                "Black",
                50,
                false,
                "PS5_pro.jpg"
        );
        dao.create(p1);

        // Act
        Product p2 = new Product(
                999,
                "PS5 pro", // Same name
                new BigDecimal("499.99"),
                1,            // Same category
                "Duplicate smartphone entry.",
                "Black",
                50,
                false,
                "PS5_pro_duplicate.jpg"
        );

        //assert
        Exception exception = assertThrows(RuntimeException.class, () -> dao.create(p2));
        assertEquals("A product with the same name and category already exists.", exception.getMessage());
    }

    @Test
    public void test_case_update_product() {
        // arrange: Create a new product
        int productId = 1;
        Product p1 = new Product(
                productId,
                "Old Smartphone",
                new BigDecimal("299.99"),
                1,
                "An old smartphone.",
                "Red",
                20,
                false,
                "old_smartphone.jpg"
        );

        // insert the initial product into the database (you could use your existing create method)
        dao.create(p1);

        // New product data for update
        Product p2 = new Product(
                productId,
                "Updated Smartphone",
                new BigDecimal("399.99"),
                1,
                "A powerful and feature-rich smartphone.",
                "Blue",
                50,
                true,
                "updated_smartphone.jpg"
        );

        // Act: Update the product using the update method
        dao.update(productId, p2);

        // Assert: Retrieve the updated product from the database
        Product productFromDb = dao.getById(productId);

        // Verify that the product's attributes have been updated correctly
        assertNotNull(productFromDb, "The product should not be null.");
        assertEquals(p2.getName(), productFromDb.getName(), "The name should be updated.");
        assertEquals(p2.getPrice(), productFromDb.getPrice(), "The price should be updated.");
        assertEquals(p2.getCategoryId(), productFromDb.getCategoryId(), "The category ID should be updated.");
        assertEquals(p2.getDescription(), productFromDb.getDescription(), "The description should be updated.");
        assertEquals(p2.getColor(), productFromDb.getColor(), "The color should be updated.");
        assertEquals(p2.getStock(), productFromDb.getStock(), "The stock should be updated.");
        assertEquals(p2.isFeatured(), productFromDb.isFeatured(), "The featured flag should be updated.");
        assertEquals(p2.getImageUrl(), productFromDb.getImageUrl(), "The image URL should be updated.");
    }

    @Test
    public void test_case_delete_product() {
        // Arrange: Insert a product into the database for deletion
        Product p1 = new Product(
                0,  // Assuming the product ID is auto-generated
                "Test Product",
                new BigDecimal("149.99"),
                2,
                "A test product for deletion.",
                "Green",
                10,
                false,
                "test_product.jpg"
        );

        // Create the product and retrieve the generated ID
        Product createdProduct = dao.create(p1);
        int productIdToDelete = createdProduct.getProductId();

        // Act: Delete the product
        dao.delete(productIdToDelete);

        // Assert: Verify the product is deleted
        Product deletedProduct = dao.getById(productIdToDelete);
        assertNull(deletedProduct, "The product should no longer exist in the database after deletion.");
    }
}