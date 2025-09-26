package com.java.jwt.controller;

import com.java.jwt.dto.CartRequest;
import com.java.jwt.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @PostMapping("/addToCart")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest request){
        return cartService.addToCart(request);
    }

    @GetMapping("/getCartItems/{username}")
    public ResponseEntity<?> getCartItems(@PathVariable String username){
        return cartService.getCartItems(username);
    }



}
