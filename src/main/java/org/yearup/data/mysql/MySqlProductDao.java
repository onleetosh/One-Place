package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao
{
    public MySqlProductDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color)
    {
        List<Product> products = new ArrayList<>();

//                          **ERROR **
//        String sql = "SELECT * FROM products " +
//                "WHERE (category_id = ? OR ? = -1) " +
//                "   AND (price <= ? OR ? = -1) " +
//                "   AND (color = ? OR ? = '') ";

        String sql = """
                SELECT * FROM products
                WHERE (? IS NULL OR category_id = ?)
                  AND (? IS NULL OR price >= ?)
                  AND (? IS NULL OR price <= ?)
                  AND (? IS NULL OR color = ?);
                    """;



        //                 ** ERROR
//        categoryId = categoryId == null ? -1 : categoryId;
//        minPrice = minPrice == null ? new BigDecimal("-1") : minPrice;
//        maxPrice = maxPrice == null ? new BigDecimal("-1") : maxPrice;
//        color = color == null ? "" : color;


        categoryId = categoryId == null ? -1 : categoryId;
        minPrice = minPrice == null ? new BigDecimal("-1") : minPrice;
        maxPrice = maxPrice == null ? new BigDecimal("-1") : maxPrice;
        color = color == null ? "" : color;



        try (Connection connection = getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement(sql);

            // Set the parameters
            stmt.setInt(1, categoryId); // for categoryId
            stmt.setInt(2, categoryId); // for categoryId (again)
            stmt.setBigDecimal(3, minPrice); // for minPrice
            stmt.setBigDecimal(4, minPrice); // for minPrice (again)
            stmt.setBigDecimal(5, maxPrice); // for maxPrice
            stmt.setBigDecimal(6, maxPrice); // for maxPrice (again)
            stmt.setString(7, color); // for color
            stmt.setString(8, color); // for color (again)
            ResultSet row = stmt.executeQuery();

            while (row.next())
            {
                Product product = mapRow(row);
                products.add(product);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return products;
    }

    @Override
    public List<Product> listByCategoryId(int categoryId)
    {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products WHERE category_id = ? ";

        try (Connection connection = getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, categoryId);

            ResultSet row = stmt.executeQuery();

            while (row.next())
            {
                Product product = mapRow(row);
                products.add(product);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return products;
    }


    @Override
    public Product getById(int productId)
    {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, productId);

            ResultSet row = statement.executeQuery();

            if (row.next())
            {
                return mapRow(row);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Product create(Product product)
    {

        String sql = """
                INSERT INTO products(name, price, category_id, description, color, image_url, stock, featured) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection connection = getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getCategoryId());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getColor());
            stmt.setString(6, product.getImageUrl());
            stmt.setInt(7, product.getStock());
            stmt.setBoolean(8, product.isFeatured());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = stmt.getGeneratedKeys();

                if (generatedKeys.next()) {
                    // Retrieve the auto-incremented ID
                    int orderId = generatedKeys.getInt(1);

                    // get the newly inserted category
                    return getById(orderId);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void update(int productId, Product product)
    {
        String sql = "UPDATE products" +
                " SET name = ? " +
                "   , price = ? " +
                "   , category_id = ? " +
                "   , description = ? " +
                "   , color = ? " +
                "   , image_url = ? " +
                "   , stock = ? " +
                "   , featured = ? " +
                " WHERE product_id = ?;";

        try (Connection connection = getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getCategoryId());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getColor());
            stmt.setString(6, product.getImageUrl());
            stmt.setInt(7, product.getStock());
            stmt.setBoolean(8, product.isFeatured());
            stmt.setInt(9, productId);

            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int productId)
    {

        String sql = "DELETE FROM products " +
                " WHERE product_id = ?;";

        try (Connection connection = getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, productId);

            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected static Product mapRow(ResultSet row) throws SQLException
    {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String color = row.getString("color");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");

        return new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);
    }
}
