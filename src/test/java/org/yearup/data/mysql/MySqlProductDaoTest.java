package org.yearup.data.mysql;

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


    /**
     * test for public Product getById(int productId)
     */
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

    /**
     * test for public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color)
     */
    @Test
    public void test_search_and_filter() {
        // Arrange
        // Insert test data using the schema provided or ensure the database is pre-populated for the test.
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
    public void listByCategoryId_shouldReturnProductsForCategory() {
        // Arrange
        int categoryId = 1; // Electronics
        Product expectedProduct1 = new Product() {{
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
        Product expectedProduct2 = new Product() {{
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
        Product expectedProduct3 = new Product() {{
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
        List<Product> expectedProducts = List.of(expectedProduct1, expectedProduct2, expectedProduct3);

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
    public void getByID_test(){
        // Arrange: Define the expected product
        int productId = 1;
        Product expectedProduct = new Product(
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
        Product product = dao.getById(productId);

        // Assert: Verify that the returned product matches the expected product
        assertNotNull(product, "The product should not be null");
        assertEquals(expectedProduct.getProductId(), product.getProductId(), "The product retrieved should match the expected product");
    }


    /**
     * Test for public Product create(Product product)
     */
    @Test
    public void test_public_Product_create_MySqlProductDao(){
        //arrange

        Product newProduct = new Product(
                0,
                "Ipad",
                new BigDecimal("199.99"),
                3,
                "A powerful and feature-rich smartphone for all your communication needs.",
                "Blue",
                50,
                false,
                "iPad.jpg" );
        //act
        Product p = dao.create(newProduct);

        Product productFromDb = dao.getById(p.getProductId());


        // assert: Verify that the created product matches the expected product
        assertNotNull(p, "The created product should not be null");
        assertEquals(newProduct.getName(), p.getName(), "The name should match");
        assertEquals(newProduct.getPrice(), p.getPrice(), "The price should match");
        assertEquals(newProduct.getCategoryId(), p.getCategoryId(), "The category ID should match");
        assertEquals(newProduct.getDescription(), p.getDescription(), "The description should match");
        assertEquals(newProduct.getColor(), p.getColor(), "The color should match");
        assertEquals(newProduct.getStock(), p.getStock(), "The stock should match");
        assertEquals(newProduct.isFeatured(), p.isFeatured(), "The featured flag should match");
        assertEquals(newProduct.getImageUrl(), p.getImageUrl(), "The image URL should match");
    }

    @Test
    public void test_updateProduct() {
        // arrange: Create a new product
        int productId = 1;
        Product initialProduct = new Product(
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
        dao.create(initialProduct);

        // New product data for update
        Product updatedProduct = new Product(
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
        dao.update(productId, updatedProduct);

        // Assert: Retrieve the updated product from the database
        Product productFromDb = dao.getById(productId);

        // Verify that the product's attributes have been updated correctly
        assertNotNull(productFromDb, "The product should not be null.");
        assertEquals(updatedProduct.getName(), productFromDb.getName(), "The name should be updated.");
        assertEquals(updatedProduct.getPrice(), productFromDb.getPrice(), "The price should be updated.");
        assertEquals(updatedProduct.getCategoryId(), productFromDb.getCategoryId(), "The category ID should be updated.");
        assertEquals(updatedProduct.getDescription(), productFromDb.getDescription(), "The description should be updated.");
        assertEquals(updatedProduct.getColor(), productFromDb.getColor(), "The color should be updated.");
        assertEquals(updatedProduct.getStock(), productFromDb.getStock(), "The stock should be updated.");
        assertEquals(updatedProduct.isFeatured(), productFromDb.isFeatured(), "The featured flag should be updated.");
        assertEquals(updatedProduct.getImageUrl(), productFromDb.getImageUrl(), "The image URL should be updated.");
    }

    @Test
    public void test_delete() {
        // Arrange: Insert a product into the database for deletion
        Product productToDelete = new Product(
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
        Product createdProduct = dao.create(productToDelete);
        int productIdToDelete = createdProduct.getProductId();

        // Act: Delete the product
        dao.delete(productIdToDelete);

        // Assert: Verify the product is deleted
        Product deletedProduct = dao.getById(productIdToDelete);
        assertNull(deletedProduct, "The product should no longer exist in the database after deletion.");
    }
}