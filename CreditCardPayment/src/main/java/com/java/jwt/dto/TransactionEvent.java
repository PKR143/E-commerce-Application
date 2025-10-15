package com.java.jwt.dto;

import com.java.jwt.entity.ProductDetailsEntity;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data @AllArgsConstructor @Builder @NoArgsConstructor @ToString
public class TransactionEvent {

    private Long txnId;
    private String username;
    private List<ProductDetailsEntity> items;
    private Double amount;
    private String address;
    private String mailId;
    private String mobileNum;
    private Date orderDate;
    private String status;
    private Long statusCode;

}
