package org.example.controllers;

import org.example.daos.OrderDao;
import org.example.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Controller for the profile of the currently logged in user.
 */
@RestController
@RequestMapping("/api/orders")
@PreAuthorize("isAuthenticated()")
public class OrderController {
    /**
     * The order data access object.
     */
    @Autowired
    private OrderDao orderDao;

    @GetMapping
    public List<Order> listOrders(@RequestParam(required = false) String username) {
    if (username != null) {
        return orderDao.getOrdersByUsername(username);
    } else {
        return orderDao.getOrders();
    }
}

    /**
     * Gets all orders.
     *
     * @return A list of all orders.
     */
    public List<Order> getAll() {
        return listOrders(null);
    }

     /**
     * Gets a order by its ID.
     *
     * @param id The ID of the order.
     * @return The order with the given ID.
     */
    @GetMapping(path = "/{id}")
    public Order get(@PathVariable int id) {
        return orderDao.getOrderById(id);
    }

     /**
     * Creates a new order.
     *
     * @param order The order to create.
     * @return The created order.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Order create(@RequestBody Order order, Principal principal) {
        String username = principal.getName();
        order.setUsername(username);

        return orderDao.createOrder(order);
    }

    /**
     * Updates an existing product.
     *
     * @param id The ID of the order to update.
     * @param order The updated order.
     * @return The updated order.
     */
    @PutMapping(path = "/{id}")
    public Order update(@PathVariable int id, @RequestBody Order order) {
        return orderDao.updateOrder(id, order);
    }

    
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {
        return orderDao.deleteOrder(id); 
    }

   
}
