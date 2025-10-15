package com.java.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRequest {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{16}", message = "Card number must be exactly 16 digits")
    private String cardNumber;

    @NotBlank(message = "User name is required")
    @Size(max = 100, message = "User name must be at most 100 characters")
    private String username;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3 or 4 digits")
    private String cvv;

    @NotBlank(message = "Expiry month is required")
    @Pattern(regexp = "0[1-9]|1[0-2]", message = "Expiry month must be between 01 and 12")
    private String expiryMonth;

    @NotBlank(message = "Expiry year is required")
    @Pattern(regexp = "\\d{4}", message = "Expiry year must be 4 digits")
    private String expiryYear;

    @NotBlank(message = "Card holder name is required")
    @Size(max = 100, message = "Card holder name must be at most 100 characters")
    private String cardHolderName;

    @NotBlank(message = "Bank name is required")
    @Size(max = 100, message = "Bank name must be at most 100 characters")
    private String bankName;

    @NotBlank(message = "Issued month is required")
    @Pattern(regexp = "0[1-9]|1[0-2]", message = "Issued month must be between 01 and 12")
    private String issuedMonth;

    @NotBlank(message = "Issued year is required")
    @Pattern(regexp = "\\d{4}", message = "Issued year must be 4 digits")
    private String issuedYear;

    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "\\d{4,6}", message = "PIN must be 4–6 digits")
    private String pin;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @NotNull(message = "Card limit is required")
    @PositiveOrZero(message = "Card limit must be zero or positive")
    private Double cardLimit;

    @NotNull(message = "Amount used is required")
    @PositiveOrZero(message = "Amount used must be zero or positive")
    private Double amountUsed;

    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address must be at most 200 characters")
    private String address;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String mailId;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^(\\+\\d{1,3})?\\d{7,15}$",
            message = "Mobile number must be 7–15 digits, optional +country code"
    )
    private String mobileNo;
}

