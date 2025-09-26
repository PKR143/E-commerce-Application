package com.java.jwt.service;

import com.java.jwt.dto.ProductRequest;
import com.java.jwt.dto.ProductResponse;
import com.java.jwt.entity.ProductDetailsEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    ResponseEntity<?> createProduct(ProductRequest request);
    ResponseEntity<?> getProductById(Long id);
    ResponseEntity<?> getAllProducts();
    ResponseEntity<?> updateProduct(Long id, ProductRequest request);
    ResponseEntity<?> deleteProduct(Long id);
}
