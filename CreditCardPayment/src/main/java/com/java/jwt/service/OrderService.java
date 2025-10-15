package com.java.jwt.service;

import com.java.jwt.dto.CardRequest;
import com.java.jwt.exception.PaymentException;
import com.java.jwt.dto.TxnRequest;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<?> doPayment(TxnRequest request)throws PaymentException, RuntimeException;
    ResponseEntity<?> checkOrder();
    ResponseEntity<?> checkCart();
    ResponseEntity<?> addCard(CardRequest card)throws PaymentException;
}
