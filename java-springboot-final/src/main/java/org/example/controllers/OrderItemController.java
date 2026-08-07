package org.example.controllers;

import org.example.daos.OrderItemDao;
import org.example.models.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for the profile of the currently logged in user.
 */
@RestController
@RequestMapping("/api/order-items")
@PreAuthorize("isAuthenticated()")
public class OrderItemController {
    /**
     * The order data access object.
     */
    @Autowired
    private OrderItemDao orderItemDao;

    @GetMapping
    public List<OrderItem> listOrderItems(@RequestParam(required = false) Integer orderId) {
        if (orderId != null) {
            return orderItemDao.getOrderItemsByOrderId(orderId.intValue());
        } else {
            return orderItemDao.getOrderItems();
        }
    }

    /**
     * Gets all orders.
     *
     * @return A list of all orders.
     */
    //@GetMapping
    public List<OrderItem> getAll() {
        return listOrderItems(null);
        //return orderItemDao.getOrderItems();
    }

     /**
     * Gets a order item by its ID.
     *
     * @param id The ID of the order item.
     * @return The order item with the given ID.
     */
    @GetMapping(path = "/{id}")
    public OrderItem get(@PathVariable int id) {
        return orderItemDao.getOrderItemById(id);
    }

     /**
     * Creates a new order item.
     *
     * @param orderItem The order item to create.
     * @return The created order.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderItem create(@RequestBody OrderItem orderItem) {
        return orderItemDao.createOrderItem(orderItem);
    }

    /**
     * Updates an existing order item.
     *
     * @param id The ID of the order item to update.
     * @param order The updated order item.
     * @return The updated order item.
     */
    @PutMapping(path = "/{id}")
    public OrderItem update(@PathVariable int id, @RequestBody OrderItem orderItem) {
        return orderItemDao.updateOrderItem(id, orderItem);
    }

    
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {
        return orderItemDao.deleteOrderItem(id); 
    }

   
}
