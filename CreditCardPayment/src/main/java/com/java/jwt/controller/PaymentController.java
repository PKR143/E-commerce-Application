package com.java.jwt.controller;

import com.java.jwt.dto.*;
import com.java.jwt.exception.PaymentException;
import com.java.jwt.service.CompleteTransactionService;
import com.java.jwt.service.MailService;
import com.java.jwt.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    OrderService service;

    @Autowired
    CompleteTransactionService txnService;

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/doPayment")
    public ResponseEntity<?> doPayment(@RequestBody @Valid TxnRequest request)throws PaymentException {
        return service.doPayment(request);
    }

    @PostMapping("/completePayment")
    public ResponseEntity<?> completePayment(@RequestBody OtpRequest request){
        return txnService.completeTxn(request.getTxnId(), request.getOtp());
    }

}
