package com.java.jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity @Data @AllArgsConstructor @NoArgsConstructor @Builder
@Table(name="product_details_entity")
public class ProductDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String productName;
    private String productType;
    private String productDescription;
    private String productSize;
    private String productColour;
    private String stockAvailable;
    private Double price;
    private Date createdDate;
    private Date updatedDate;

}
