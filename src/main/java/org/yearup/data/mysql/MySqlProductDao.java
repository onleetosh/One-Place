package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.data.interfaces.ProductDao;

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

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(Queries.selectProductsByFilter());


            // Set the parameters
            statement.setObject(1, categoryId == null ? null : categoryId); // for categoryId
            statement.setObject(2, categoryId == null ? null : categoryId); // for categoryId (again)
            statement.setObject(3, minPrice == null ? null : minPrice); // for minPrice
            statement.setObject(4, minPrice == null ? null : minPrice); // for minPrice (again)
            statement.setObject(5, maxPrice == null ? null : maxPrice); // for maxPrice
            statement.setObject(6, maxPrice == null ? null : maxPrice); // for maxPrice (again)
            statement.setObject(7, color == null ? null : color); // for color
            statement.setObject(8, color == null ? null : color); // for color (again)
            ResultSet row = statement.executeQuery();

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
            PreparedStatement statement = connection.prepareStatement(Queries.selectProductsByCatId());
            statement.setInt(1, categoryId);

            ResultSet row = statement.executeQuery();

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

    /**
     * PASSED
     */
    @Override
    public Product create(Product product)
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(Queries.insertProduct(), PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getColor());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = statement.getGeneratedKeys();

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
            PreparedStatement statement = connection.prepareStatement(Queries.updateProductByProdId());
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getColor());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());
            statement.setInt(9, productId);

            statement.executeUpdate();
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
            PreparedStatement statement = connection.prepareStatement(Queries.dropProductById());
            statement.setInt(1, productId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected static Product mapRow(ResultSet row) throws SQLException {
        return new Product(
                row.getInt("product_id"),
                row.getString("name"),
                row.getBigDecimal("price"),
                row.getInt("category_id"),
                row.getString("description"),
                row.getString("color"),
                row.getInt("stock"),
                row.getBoolean("featured"),
                row.getString("image_url")
        );
    }
}
