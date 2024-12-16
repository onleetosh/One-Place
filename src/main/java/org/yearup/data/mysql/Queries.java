package org.yearup.data.mysql;

public class Queries {

    /**
     * Categories query statements
     * @return
     */

    public static String selectCategories()
    {
        return "SELECT * FROM Categories";
    }
    public static String selectCategoriesByCatId()
    {
        return "SELECT * FROM categories WHERE category_id = ?";
    }
    public static String selectCategoriesByName()
    {
        return "SELECT * FROM categories WHERE name = ?";
    }
    public static String insertCategories()
    {
        return """
            INSERT INTO 
                categories (name, description)
            VALUES 
                (?, ?)
            """;
    }
    public static String updateCategoriesByCatId()
    {
        return """
          UPDATE categories
          SET name = ?, description = ?
          WHERE category_id = ?
          """;
    }
    public static String dropCategoriesByCatId()
    {
        return "DELETE FROM categories WHERE category_Id = ?";
    }

    /**
     * Product query statements
     */
    public static String selectProductsByFilter()
    {
        return """
          SELECT * FROM products
          WHERE (? IS NULL OR category_id = ?)
          AND (? IS NULL OR price >= ?)
          AND (? IS NULL OR price <= ?)
          AND (? IS NULL OR color = ?);
          """;
    }
    public static String selectProductsByCatId()
    {
        return "SELECT * FROM products WHERE category_id = ? ";
    }
    public static String selectProductByProdId()
    {
        return "SELECT * FROM products WHERE product_id = ?";
    }
    public static String insertProduct()
    {
        return """
          INSERT INTO 
               products(name, 
               price, 
               category_id, 
               description, 
               color, 
               image_url, 
               stock, 
               featured) 
          VALUES 
               (?, ?, ?, ?, ?, ?, ?, ?);
          """;
    }
    public static String updateProductByProdId()
    {
         return """
            UPDATE products
            SET name = ?, 
                price = ?, 
                category_id = ?, 
                description = ?, 
                color = ?, 
                image_url = ?, 
                stock = ?, 
                featured = ? 
            WHERE product_id = ?;
            """;
    }
    public static String dropProductById()
    {
        return "DELETE FROM products WHERE product_id = ?";
    }

    /**
     * Shopping cart query statements
     * @return
     */
    public static String selectProductByUserId()
    {
        return """
            SELECT
                products.product_id,
                products.name,
                products.price,
                products.category_id,
                products.description,
                products.color,
                products.stock,
                products.featured,
                products.image_url,
                shopping_cart.quantity
            FROM
                shopping_cart
            JOIN
                products
            ON
                shopping_cart.product_id = products.product_id
            WHERE
                shopping_cart.user_id = ?;
            """;
    }
    public static String selectQuantity()
    {
        return """
                SELECT quantity FROM shopping_cart 
                WHERE user_id = ? AND product_id = ?
                """;
    }
    public static String updateShoppingCart()
    {
        return """
                UPDATE shopping_cart 
                SET quantity = ? 
                WHERE user_id = ? AND product_id = ?
                """;
    }
    public static String dropShoppingCart()
    {
        return "DELETE FROM shopping_cart WHERE user_id = ?";
    }

    /**
     * Profile query statements
     */
    public static String insertProfile()
    {
        return """
                INSERT INTO 
                    profiles (user_id, 
                    first_name,
                    last_name, 
                    phone, 
                    email, 
                    address, 
                    city, 
                    state,
                    zip)
                VALUES 
                    (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
    }
    public static String selectProfileById()
    {
        return "SELECT * FROM profiles WHERE user_id = ?";

    }
    public static String updateProfile()
    {
        return """
                UPDATE profiles 
                SET first_name = ?, 
                    last_name = ?, 
                    phone = ?, 
                    email = ?, 
                    address = ?, 
                    city = ?, 
                    state = ?, 
                    zip = ? 
                WHERE user_id = ?
                """;
    }
}
