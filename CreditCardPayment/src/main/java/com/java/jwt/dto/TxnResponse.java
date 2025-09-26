package com.java.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TxnResponse implements Response{

    private Long txnId;
    private String cardNumber;
    private Double requestAmount;
    private String cardHolderName;
    private String cardType;
    private String mobileNo;
}
