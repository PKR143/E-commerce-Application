package com.java.jwt.exception;

public class PaymentException extends RuntimeException{
    public PaymentException(String errorMsg){
        super(errorMsg);
    }

}
