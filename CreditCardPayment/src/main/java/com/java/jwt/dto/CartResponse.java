package com.java.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CartResponse implements Response{
    private String username;
    private Long productId;
    private String productName;
    private String productDescription;
    private String productSize;
    private String productColour;
    private String stockAvailable;
    private Double price;
}
