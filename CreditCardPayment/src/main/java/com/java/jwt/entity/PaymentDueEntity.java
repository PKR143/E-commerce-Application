package com.java.jwt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Builder @NoArgsConstructor @AllArgsConstructor @ToString
@Table(name = "credit_card_due_scheduler_dates")
public class PaymentDueEntity {

    @Id
    private String cardNumber;

    private String username;
    private String cvv;
    private String cardHolderName;
    private String bankName;
    private Boolean isActive;
    private String address;
    private String mailId;
    private String mobileNo;
    private LocalDate dueDate;
    private Double dueAmount;


}
