package com.java.jwt.service;

import com.java.jwt.dto.GeneralResponse;
import com.java.jwt.dto.ProductRequest;
import com.java.jwt.dto.ProductResponse;
import com.java.jwt.entity.ProductDetailsEntity;
import com.java.jwt.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);


    @Override
    public ResponseEntity<?> createProduct(ProductRequest request) {
        logger.info("Creating product: {}", request.getProductName());
        ProductDetailsEntity entity = ProductDetailsEntity.builder()
                .productName(request.getProductName())
                .productType(request.getProductType())
                .productDescription(request.getProductDescription())
                .productSize(request.getProductSize())
                .productColour(request.getProductColour())
                .stockAvailable(request.getStockAvailable())
                .price(request.getPrice())
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();
        ProductDetailsEntity saved = productRepository.save(entity);
        logger.debug("Product created with ID: {}", saved.getProductId());
        return ResponseEntity.ok(new GeneralResponse(toResponse(saved), 1L, "Product saved."));
    }


    @Override
    public ResponseEntity<?> getProductById(Long id) {
        logger.info("Fetching product by id: {}", id);
        return productRepository.findById(id)
                .map(entity -> ResponseEntity.ok(new GeneralResponse(toResponse(entity), 1L, "Product Exists.")))
                .orElseGet(() -> {
                    logger.warn("Product not found with id: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GeneralResponse(null, -1L, "Product not exists."));
                });
    }

    @Override
    public ResponseEntity<?> getAllProducts() {
        logger.info("Fetching all products");
        List<ProductResponse> responses = productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<?> updateProduct(Long id, ProductRequest request) {
        logger.info("Updating product with id: {}", id);
        return productRepository.findById(id)
                .map(entity -> {
                    entity.setProductName(request.getProductName());
                    entity.setProductType(request.getProductType());
                    entity.setProductDescription(request.getProductDescription());
                    entity.setProductSize(request.getProductSize());
                    entity.setProductColour(request.getProductColour());
                    entity.setStockAvailable(request.getStockAvailable());
                    entity.setPrice(request.getPrice());
                    entity.setUpdatedDate(new Date());
                    ProductDetailsEntity updated = productRepository.save(entity);
                    logger.debug("Product updated with id: {}", updated.getProductId());
                    return ResponseEntity.ok(new GeneralResponse(toResponse(updated),1L, "Product updated."));
                })
                .orElseGet(() -> {
                    logger.warn("Cannot update. Product not found with id: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GeneralResponse(null, -1L, "Product not exists."));
                });
    }

    @Override
    public ResponseEntity<?> deleteProduct(Long id) {
        logger.info("Deleting product with id: {}", id);
        return productRepository.findById(id)
                .map(entity -> {
                    productRepository.delete(entity);
                    logger.debug("Product deleted with id: {}", id);
                    return ResponseEntity.ok(new GeneralResponse(toResponse(entity), 1L, "Product Deleted."));
                })
                .orElseGet(() -> {
                    logger.warn("Cannot delete. Product not found with id: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GeneralResponse(null, -1L, "Product not exists."));
                });
    }

    /** Converts entity to response DTO */
    private ProductResponse toResponse(ProductDetailsEntity entity) {
        return ProductResponse.builder()
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .productType(entity.getProductType())
                .productDescription(entity.getProductDescription())
                .productSize(entity.getProductSize())
                .productColour(entity.getProductColour())
                .stockAvailable(entity.getStockAvailable())
                .price(entity.getPrice())
                .build();
    }
}
