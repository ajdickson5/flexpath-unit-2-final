package org.example.controllers;

import org.example.daos.ProductDao;
import org.example.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for the profile of the currently logged in user.
 */
@RestController
@RequestMapping("/api/products")
@PreAuthorize("isAuthenticated()")
public class ProductController {
    /**
     * The product data access object.
     */
    @Autowired
    private ProductDao productDao;

    /**
     * Gets all products.
     *
     * @return A list of all products.
     */
    @GetMapping
    public List<Product> getAll() {
        return productDao.getProducts();
    }

     /**
     * Gets a product by its ID.
     *
     * @param id The ID of the product.
     * @return The product with the given ID.
     */
    @GetMapping(path = "/{id}")
    public Product get(@PathVariable int id) {
        return productDao.getProductById(id);
    }

     /**
     * Creates a new product.
     *
     * @param product The product to create.
     * @return The created product.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productDao.createProduct(product);
    }

    /**
     * Updates an existing product.
     *
     * @param id The ID of the product to update.
     * @param product The updated product.
     * @return The updated product.
     */
    @PutMapping(path = "/{id}")
    public Product update(@PathVariable int id, @RequestBody Product product) {
        return productDao.updateProduct(id, product);
    }
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {
        return productDao.deleteProduct(id);
    }

   
}
