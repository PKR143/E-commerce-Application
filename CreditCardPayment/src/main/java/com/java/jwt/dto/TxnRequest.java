package com.java.jwt.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TxnRequest {

    @NotBlank(message = "Username is required for transaction")
    private String username;

    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;

//    @Pattern(
//            regexp = "^[+]?(?:\\d+(?:\\.\\d+)?|\\.\\d+)$",
//            message = "Invalid amount sent in request"
//    )
//    @NotBlank(message = "Request amount is required")
//    private String requestAmount;


    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3 or 4 digits")
    private String cvv;


    @NotBlank(message = "Expiry month is required")
    @Pattern(regexp = "0[1-9]|1[0-2]", message = "Expiry month must be 01–12")
    private String expiryMonth;

    @NotBlank(message = "Expiry year is required")
    @Pattern(regexp = "\\d{4}", message = "Expiry year must be 4 digits")
    private String expiryYear;

    @NotBlank(message = "Card holder name is required")
    @Size(max = 100, message = "Card holder name must be at most 100 characters")
    private String cardHolderName;


    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "\\d{4,6}", message = "PIN must be 4–6 digits")
    private String pin;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^(\\+\\d{1,3})?\\d{7,15}$",
            message = "Mobile number must be 7–15 digits, optional +country code"
    )
    private String mobileNo;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

}
