package com.java.jwt.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must be at most 100 characters")
    private String productName;

    @NotBlank(message = "Product type is required")
    @Size(max = 50, message = "Product type must be at most 50 characters")
    private String productType;

    @NotBlank(message = "Product description is required")
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String productDescription;

    @NotBlank(message = "Product size is required")
    @Size(max = 20, message = "Size must be at most 20 characters")
    private String productSize;

    @NotBlank(message = "Product colour is required")
    @Size(max = 30, message = "Colour must be at most 30 characters")
    private String productColour;

    @NotBlank(message = "Stock availability is required")
    @Pattern(regexp = "^[0-9]+$", message = "Stock available must be a numeric string")
    private String stockAvailable;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;

}
