package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.exceptions.NotFoundException;
import org.example.models.Product;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

/**
 * Data access object for products.
 */
@Component
public class ProductDao {
    /**
     * The JDBC template for querying the database.
     */
    private final JdbcTemplate jdbcTemplate;


    /**
     * Creates a new products data access object.
     *
     * @param dataSource The data source for the DAO.
     */
    public ProductDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all products.
     *
     * @return List of Product
     */
    public List<Product> getProducts() {
        return jdbcTemplate.query("SELECT * FROM products ORDER BY id;", this::mapToProduct);
    }

    /**
     * Gets a product by id.
     *
     * @param id The id of the product.
     * @return Product
     */
    public Product getProductById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM products WHERE id = ?", this::mapToProduct, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Product id " + id + " not found.");
        }
    }

    /**
     * Creates a new product.
     * @param product The product to create.
     * @return Product The created product.
     */
    public Product createProduct(Product product) {
        String sql = "INSERT INTO products (name, price) VALUES (?,?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                // Provide Statement.RETURN_GENERATED_KEYS to the connection
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, product.getName());
                ps.setBigDecimal(2, product.getPrice());
                return ps;
            }, keyHolder);
            // Retrieve the map representing the returned row
            Map<String, Object> keys = keyHolder.getKeys();

            if (keys != null && !keys.isEmpty()) {
                Object firstValue = keys.values().iterator().next();
                if (firstValue instanceof Number) {
                    int id =  ((Number) firstValue).intValue();
                    return getProductById(id);
                }
            }
            return null;
        } catch (EmptyResultDataAccessException e) {
            throw new DaoException("Failed to create product");
        }

    }

    /**
     * Updates an existing product.
     *
     * @param id The id of the product to update.
     * @param product The updated product.
     * @return Product The updated product.
     */
    public Product updateProduct(int id, Product product) {
        String sql = "UPDATE products SET name = ?, price = ? WHERE id = ?;";
        try {
            int rowsAffected = jdbcTemplate.update(sql, product.getName(), product.getPrice(), id);
            if (rowsAffected == 0) {
                throw new NotFoundException("Product id " + id + " not found.");
            }
            return getProductById(id);
        } catch (DataAccessException e) {
            throw new DaoException("Failed to update product.");
        }
    }

    /**
     * Deletes a product.
     *
     * @param id The id of the product.
     */
    public int deleteProduct(int id) {
        try {
            String sql = "DELETE FROM products WHERE id = ? ";
            int rowsAffected = jdbcTemplate.update(sql, id);
            if (rowsAffected == 0) {
                throw new NotFoundException("Product id " + id + " not found.");
            }
            return rowsAffected;
        } catch (DataAccessException e) {
            throw new DaoException("Failed to delete product.");
        }
    }

    /**
     * Maps a row in the ResultSet to a product object.
     *
     * @param resultSet The result set to map.
     * @param rowNumber The row number.
     * @return Product The product object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private Product mapToProduct(ResultSet resultSet, int rowNumber) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        BigDecimal price = resultSet.getBigDecimal("price");
        return new Product(
                id,
                name,
                price
        );
    }
}
