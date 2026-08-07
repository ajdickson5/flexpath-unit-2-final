package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.exceptions.NotFoundException;
import org.example.models.OrderItem;
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
public class OrderItemDao {
    /**
     * The JDBC template for querying the database.
     */
    private final JdbcTemplate jdbcTemplate;


    /**
     * Creates a new order items data access object.
     *
     * @param dataSource The data source for the DAO.
     */
    public OrderItemDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all order items.
     *
     * @return List of OrderItem
     */
    public List<OrderItem> getOrderItems() {
        return jdbcTemplate.query("SELECT * FROM order_items ORDER BY id;", this::mapToOrderItem);
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        return jdbcTemplate.query("SELECT * FROM order_items WHERE order_id = ? ORDER BY id;", this::mapToOrderItem, orderId);
    }

    /**
     * Gets an order item by id.
     *
     * @param id The id of the order item.
     * @return OrderItem
     */
    public OrderItem getOrderItemById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM order_items WHERE id = ?", this::mapToOrderItem, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Order item id " + id + " not found.");
        }
    }

    /**
     * Creates a new order item.
     * @param orderItem The order item to create.
     * @return OrderItem The created order item.
     */
    public OrderItem createOrderItem(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_id,product_id, quantity) VALUES (?,?,?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                // Provide Statement.RETURN_GENERATED_KEYS to the connection
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, orderItem.getOrderId());
                ps.setInt(2, orderItem.getProductId());
                ps.setInt(3, orderItem.getQuantity());
                return ps;
            }, keyHolder);
            // Retrieve the map representing the returned row
            Map<String, Object> keys = keyHolder.getKeys();

            if (keys != null && !keys.isEmpty()) {
                Object firstValue = keys.values().iterator().next();
                if (firstValue instanceof Number) {
                    int id =  ((Number) firstValue).intValue();
                    return getOrderItemById(id);
                }
            }
            return null;
        } catch (EmptyResultDataAccessException e) {
            throw new DaoException("Failed to create product");
        }

    }

    /**
     * Updates an existing order item.
     *
     * @param id The id of the order item to update.
     * @param orderItem The updated orderItem.
     * @return OrderItem The updated order item.
     */
    public OrderItem updateOrderItem(int id, OrderItem orderItem) {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ? WHERE id = ?;";
        try {
            int rowsAffected = jdbcTemplate.update(sql, orderItem.getOrderId(), orderItem.getProductId(), orderItem.getQuantity(), id);
            if (rowsAffected == 0) {
                throw new NotFoundException("Order item id " + id + " not found.");
            }
            return getOrderItemById(id);
        } catch (DataAccessException e) {
            throw new DaoException("Failed to update order item.");
        }
    }

    /**
     * Deletes a product.
     *
     * @param id The id of the product.
     */
    public int deleteOrderItem(int id) {
        try {
            String sql = "DELETE FROM order_items WHERE id = ? ";
            int rowsAffected = jdbcTemplate.update(sql, id);
            if (rowsAffected == 0) {
                throw new NotFoundException("Order item id " + id + " not found.");
            }
            return rowsAffected;
        } catch (DataAccessException e) {
            throw new DaoException("Failed to delete order item.");
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
    private OrderItem mapToOrderItem(ResultSet resultSet, int rowNumber) throws SQLException {
        int id = resultSet.getInt("id");
        int orderId = resultSet.getInt("order_id");
        int productId = resultSet.getInt("product_id");
        int quantity = resultSet.getInt("quantity");
        return new OrderItem(
                id,
                orderId,
                productId,
                quantity
        );
    }
}
