package com.java.jwt.service;

import com.java.jwt.dto.CartRequest;
import org.springframework.http.ResponseEntity;

public interface CartService {
    ResponseEntity<?> addToCart(CartRequest cart);
    ResponseEntity<?>  getCartItems(String username);
}
