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
            PreparedStatement stmt = connection.prepareStatement(Queries.selectProductsByFilter());

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

        try (Connection connection = getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement(Queries.selectProductsByCatId());
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
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(Queries.selectProductByProdId());
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

        try (Connection connection = getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement(Queries.insertProduct(), PreparedStatement.RETURN_GENERATED_KEYS);
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

        try (Connection connection = getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement(Queries.updateProductByProdId());
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
        try (Connection connection = getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement(Queries.dropProductById());
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
