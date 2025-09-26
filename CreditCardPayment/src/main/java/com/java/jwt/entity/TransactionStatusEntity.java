package com.java.jwt.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "transaction_status_entity")
public class TransactionStatusEntity {

    @Id
    private Long txnId;
    private String userName;
    private Double requestAmount;
    private String cardNumber;
    private String cvv;
    private String expiryMonth;
    private String expiryYear;
    private String cardHolderName;
    private String bankName;
    private String issuedMonth;
    private String issuedYear;
    private String pin;
    private Boolean isActive;
    private String address;
    private String mailId;
    private String mobileNo;
    private String otp;
    private String otpCreatedTime;

    private Integer statusCode;
    private String status;

}
