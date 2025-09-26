package com.java.jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor @NoArgsConstructor @Builder
@Data
@Table(name = "card_details_entity")
public class CardDetailsEntity {

    @Id
    private String cardNumber;

    private String userName;
    private String cvv;
    private String expiryMonth;
    private String expiryYear;
    private String cardHolderName;
    private String bankName;
    private String issuedMonth;
    private String issuedYear;
    private String pin;
    private Boolean isActive;
    private Double cardLimit;
    private Double amountUsed;
    private String address;
    private String mailId;
    private String mobileNo;

//    @ManyToOne
//    @JoinColumn(name="userName")
//    private User user;

}
