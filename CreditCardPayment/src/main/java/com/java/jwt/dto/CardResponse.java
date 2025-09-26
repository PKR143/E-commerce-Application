package com.java.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class CardResponse implements Response{
    private String cardNumber;
    private String cardHolderName;
}
