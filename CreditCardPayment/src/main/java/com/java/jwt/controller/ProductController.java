package com.java.jwt.controller;

import com.java.jwt.dto.ProductRequest;
import com.java.jwt.dto.ProductResponse;
import com.java.jwt.entity.ProductDetailsEntity;
import com.java.jwt.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid ProductRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/allProducts")
    public ResponseEntity<?> getAll() {
        return productService.getAllProducts();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                                       @RequestBody @Valid ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }


}
