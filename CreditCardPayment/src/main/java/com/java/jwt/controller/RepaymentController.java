package com.java.jwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.jwt.dto.GeneralResponse;
import com.java.jwt.entity.CardDetailsEntity;
import com.java.jwt.entity.PaymentDueEntity;
import com.java.jwt.repository.CardRepository;
import com.java.jwt.repository.RepaymentRepository;
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

    private static final Logger logger = LoggerFactory.getLogger(RepaymentController.class);

//    @Scheduled(cron = "0 * * * * *")
    public void runEveryTime(){
        System.out.println("card number: 2223623487172, due Amount: "+12_500);
    }

    @Scheduled(cron = "0 * * * * *")
    public void rePayment(){
        try {
            logger.info("notification regarding pending dues");
            List<PaymentDueEntity> dues = repo.findAll();


            for(PaymentDueEntity due : dues){
                if(due.getDueDate().equals(LocalDate.now())){
                    System.out.println(due.getCardHolderName()+",card number: "+due.getCardNumber()+" has  a due of "+due.getDueAmount()+" on "+due.getDueDate());
                }
            }

        }catch(Exception e){
            logger.info("exception while sending repayment notification");
        }
    }

    @PostMapping
    public ResponseEntity<?> addCards(@RequestBody List<String> cards){
        logger.info("request to add cards for repayment");
        List<String> validCards = new ArrayList<>();
        List<String> inValidCards = new ArrayList<>();
        Optional<CardDetailsEntity> currentCard;
        CardDetailsEntity cardDetailsEntity;
        PaymentDueEntity paymentDueEntity;
        try {
            for (String card : cards) {

                currentCard = cardRepo.findById(card);

                if (currentCard.isEmpty()) {
                    inValidCards.add(card);
                    continue;
                }
                validCards.add(card);
                cardDetailsEntity = currentCard.get();
                paymentDueEntity = mapToEntity(cardDetailsEntity);
                repo.save(paymentDueEntity);
            }
            logger.info("valid cards: {}",validCards);
            logger.info("invalid cards: {}",inValidCards);
            return ResponseEntity.status(HttpStatus.CREATED).body(List.of(validCards,inValidCards));
        }catch (Exception e){
            logger.info("exception @addCards in Repayment controller");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralResponse(null, -1L, "invalid cards entered, please try with valid cards"));
        }

    }

    private PaymentDueEntity mapToEntity(CardDetailsEntity card) {
        return PaymentDueEntity.builder()
                .cardNumber(card.getCardNumber())
                .userName(card.getUserName())
                .cvv(card.getCvv())
                .cardHolderName(card.getCardHolderName())
                .bankName(card.getBankName())
                .isActive(card.getIsActive())
                .address(card.getAddress())
                .mailId(card.getMailId())
                .mobileNo(card.getMobileNo())
                .dueDate(LocalDate.now())
                .dueAmount(card.getAmountUsed())
                .build();
    }

}
