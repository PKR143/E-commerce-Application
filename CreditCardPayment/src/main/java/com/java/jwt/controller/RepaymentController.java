package com.java.jwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.jwt.dto.GeneralResponse;
import com.java.jwt.entity.CardDetailsEntity;
import com.java.jwt.entity.PaymentDueEntity;
import com.java.jwt.exception.PaymentException;
import com.java.jwt.repository.CardRepository;
import com.java.jwt.repository.RepaymentRepository;
import com.java.jwt.service.RePaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/repay")
public class RepaymentController {

    @Autowired
    CardRepository cardRepo;

    @Autowired
    RepaymentRepository repo;

    @Autowired
    RePaymentService rePaymentService;

    private static final Logger logger = LoggerFactory.getLogger(RepaymentController.class);

//    @Scheduled(cron = "0 * * * * *")
    public void runEveryTime(){
        System.out.println("card number: 2223623487172, due Amount: "+12_500);
    }

    @Scheduled(cron = "0 * * * * *")
    public void rePayment(){
        try {
            logger.info("notification regarding pending dues");
            List<String> cards = rePaymentService.findCards(cardRepo.findAll());
            rePaymentService.addCards(cards);
            rePaymentService.rePay();
        }catch(Exception e){
            logger.info("exception while sending repayment notification");
        }
    }

    @PostMapping
    public ResponseEntity<?> addCards(@RequestBody List<String> cards){
        logger.info("request to add cards for repayment");
        try{
            List<List<String>> list = rePaymentService.addCards(cards);
            if(list.isEmpty()) throw new PaymentException("Something went wrong!");
            return ResponseEntity.status(HttpStatus.CREATED).body(List.of(list.get(0),list.get(1)));
        }catch (Exception e){
            logger.info("exception @addCards in Repayment controller");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralResponse(null, -1L, "invalid cards entered, please try with valid cards"));
        }

    }


}
