package com.java.jwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.jwt.dto.*;
import com.java.jwt.exception.PaymentException;
import com.java.jwt.service.MailService;
import com.java.jwt.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    OrderService service;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MailService mailService;

    private  static  final Logger logger = LoggerFactory.getLogger(OrderController.class);

//    @PostMapping("/payment")
//    public ResponseEntity<?> doPayment(@RequestBody @Valid TxnRequest request)throws PaymentException {
//
//        ResponseEntity<?> response = service.doPayment(request);
//        try {
//            logger.info("calling mail service");
//            Object object = response.getBody();
//            if (object instanceof GeneralResponse) {
//                GeneralResponse generalResponse = (GeneralResponse) object;
//                TxnResponse txnResponse = (TxnResponse) generalResponse.getResponse();
//                MailRequest mailRequest = MailRequest.builder()
//                        .to(request.getEmail())
//                        .subject("Transaction Bill")
//                        .body(generateMessage(txnResponse, generalResponse.getStatusDesc()))
//                        .build();
//                MailResponse mailResponse = mailService.sendMail(mailRequest);
//                if(mailResponse.getStatus() == -1L){
//                    logger.info(request.getEmail()+" exception while sending mail");
//                }
//            }
//        } catch (Exception e) {
//            logger.info("exception @soPayment while calling mail service");
//            throw new RuntimeException(e);
//        }
//        return response;
//    }
//
//    private String generateMessage(TxnResponse txn, String statusDesc) {
//
//        return String.format(
//                "Hello %s,%n%n" +
//                        "Thank you for your transaction.%n" +
//                        "Here are your details:%n" +
//                        "Transaction ID : %s%n" +
//                        "Amount         : %s%n" +
//                        "Mobile Number  : %s%n%n" +
//                        "Message: %s%n%n" +
//                        "Best regards,%niServeU pvt. ltd.",
//                txn.getCardHolderName(),
//                txn.getTxnId(),
//                txn.getRequestAmount(),
//                txn.getMobileNo(),
//                statusDesc
//        );
//
//    }

    @GetMapping("/checkOrder")
    public ResponseEntity<?> checkOrder(){
        return service.checkOrder();
    }

    @GetMapping("/cart")
    public ResponseEntity<?> checkCart(){
        return service.checkCart();
    }

    @PostMapping("/addCard")
    public ResponseEntity<?> addCard(@RequestBody @Valid CardRequest card)throws PaymentException {
        card.setPin(passwordEncoder.encode(card.getPin()));
        return service.addCard(card);
    }

}
