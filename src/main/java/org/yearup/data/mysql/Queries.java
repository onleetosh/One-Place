package org.yearup.data.mysql;

public class Queries {

    /**
     * Categories query statements
     * @return
     */

    public static String selectCategories(){
        return "SELECT * FROM Categories";
    }

    public static String selectCategoriesById(){
        return "SELECT * FROM categories WHERE category_id = ?";
    }
    public static String selectCategoriesByName(){
        return "SELECT * FROM categories WHERE name = ?";
    }


    public static String insertCategories(){
        return """
            INSERT INTO 
                categories (name, description)
            VALUES 
                (?, ?)
            """;
    }

    public static String updateCategories(){
        return """
              UPDATE categories
              SET name = ?, description = ?
              WHERE category_id = ?
              """;
    }

    public static String dropCategories(){
        return "DELETE FROM categories WHERE category_Id = ?";
    }

    /**
     * Shopping cart query statements
     * @return
     */
    public static String selectProductById(){
        return """
            SELECT
                products.product_id,
                products.name,
                products.price,
                products.category_id,
                products.description,
                products.color,
                products.stock,
                products.featured AS is_featured,
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

    public static String selectQuantity(){
        return """
                SELECT quantity FROM shopping_cart 
                WHERE user_id = ? AND product_id = ?
                """;
    }

    public static String updateShoppingCart(){
        return """
                UPDATE shopping_cart SET quantity = ? 
                WHERE user_id = ? AND product_id = ?
                """;
    }

    public static String dropShoppingCart(){
        return """
                DELETE FROM shopping_cart WHERE user_id = ?
                """;
    }

}
