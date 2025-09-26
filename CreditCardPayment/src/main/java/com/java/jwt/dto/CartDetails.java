package com.java.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CartDetails {

    private String username;
    private List<ProductDetails> items;
    private Double amount;
}
