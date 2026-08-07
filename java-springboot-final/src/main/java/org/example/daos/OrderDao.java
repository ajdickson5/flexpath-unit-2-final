package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.exceptions.NotFoundException;
import org.example.models.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;

/**
 * Data access object for orders.
 */
@Component
public class OrderDao {
    /**
     * The JDBC template for querying the database.
     */
    private final JdbcTemplate jdbcTemplate;


    /**
     * Creates a new orders data access object.
     *
     * @param dataSource The data source for the DAO.
     */
    public OrderDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all orders.
     *
     * @return List of Order
     */
    public List<Order> getOrders() {
        return jdbcTemplate.query("SELECT * FROM orders ORDER BY id;", this::mapToOrder);
    }

    public List<Order> getOrdersByUsername(String username) {
        return jdbcTemplate.query("SELECT * FROM orders WHERE username = ? ORDER BY id;", this::mapToOrder, username);
    }

    

    /**
     * Gets a order by id.
     *
     * @param id The id of the order.
     * @return Order
     */
    public Order getOrderById(int id) {
        try {
            Object o = jdbcTemplate.queryForObject("SELECT * FROM orders WHERE id = ?", this::mapToOrder, id);
            return (Order) o;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Order id " + id + " not found.");
        }
    }

    /**
     * Creates a new order.
     * @param order The order to create.
     * @return Order The created order.
     */
    public Order createOrder(Order order) {
        String sql = "INSERT INTO orders (username) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                // Provide Statement.RETURN_GENERATED_KEYS to the connection
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, order.getUsername());
                return ps;
            }, keyHolder);
            // Retrieve the map representing the returned row
            Map<String, Object> keys = keyHolder.getKeys();

            if (keys != null && !keys.isEmpty()) {
                Object firstValue = keys.values().iterator().next();
                if (firstValue instanceof Number) {
                    int id =  ((Number) firstValue).intValue();
                    return getOrderById(id);
                }
            }
            return null;

        } catch (EmptyResultDataAccessException e) {
            throw new DaoException("Failed to create order.");
        }
    }

    /**
     * Updates an existing order.
     *
     * @param id The id of the order to update.
     * @param order The updated order.
     * @return Order The updated order.
     */
    public Order updateOrder(int id, Order order) {
        String sql = "UPDATE orders SET username = ? WHERE id = ?;";
        try {
            int rowsAffected = jdbcTemplate.update(sql, order.getUsername(), id);
            if (rowsAffected == 0) {
                throw new NotFoundException("Order id " + id + " not found.");
            }
            return getOrderById(id);
        } catch (DataAccessException e) {
            throw new DaoException("Failed to update order.");
        }
    }

    /**
     * Deletes a order.
     *
     * @param id The id of the order.
     */
    public int deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE id = ? ";
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            if (rowsAffected == 0) {
                throw new NotFoundException("Order id " + id + " not found.");
            }
            return rowsAffected;
        } catch (DataAccessException e) {
            throw new DaoException("Failed to delete order.");
        }
    }

    /**
     * Maps a row in the ResultSet to a order object.
     *
     * @param resultSet The result set to map.
     * @param rowNumber The row number.
     * @return order The order object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private Order mapToOrder(ResultSet resultSet, int rowNumber) throws SQLException {
        int id = resultSet.getInt("id");
        String username = resultSet.getString("username");
        return new Order(
                id,
                username
        );
    }


}
