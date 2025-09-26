package com.java.jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Entity @AllArgsConstructor @NoArgsConstructor @Builder
@Table(name = "user_cart_details_entity")
public class UserCartDetailsEntity {

    @Id
    private String username;

    @ManyToMany
    private List<ProductDetailsEntity> items;

    private Double amount;

}
