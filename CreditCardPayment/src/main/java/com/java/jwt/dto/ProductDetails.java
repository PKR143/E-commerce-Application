package com.java.jwt.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ProductDetails {
    private String productName;
    private String productType;
    private Double price;
}
