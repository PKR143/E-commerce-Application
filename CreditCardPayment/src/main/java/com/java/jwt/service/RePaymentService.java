package com.java.jwt.service;

import com.java.jwt.entity.CardDetailsEntity;
import com.java.jwt.entity.PaymentDueEntity;
import com.java.jwt.repository.CardRepository;
import com.java.jwt.repository.RepaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RePaymentService {

    @Autowired
    CardRepository cardRepo;

    @Autowired
    RepaymentRepository repository;

    public void rePay() {
        log.info("processing repayment of saved cards.");
        List<PaymentDueEntity> dueEntities = repository.findAll();
        dueEntities.forEach(due -> due.setDueDate(LocalDate.now()));
        dueEntities.stream().filter(due -> due.getDueDate().equals(LocalDate.now())).forEach(due -> System.out.println(due.getCardHolderName() + ",card number: " + due.getCardNumber() + " has  a due of " + due.getDueAmount() + " on " + due.getDueDate()));
    }

    public List<List<String>> addCards(List<String> cards) {
        List<String> validCards = new ArrayList<>();
        List<String> inValidCards = new ArrayList<>();
        List<List<String>> list = new ArrayList<>();
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
                repository.save(paymentDueEntity);
            }
            log.info("valid cards: {}", validCards);
            log.info("invalid cards: {}", inValidCards);
            list.add(validCards);
            list.add(inValidCards);
            return list;
        } catch (Exception e) {
            log.info("exception while adding cards to repayment entity. : {}",e.getMessage());
            return new ArrayList<>();
        }
    }
    private PaymentDueEntity mapToEntity(CardDetailsEntity card) {
        return PaymentDueEntity.builder()
                .cardNumber(card.getCardNumber())
                .username(card.getUsername())
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

    public List<String> findCards(List<CardDetailsEntity> cardEntities){
        List<String> cards = new ArrayList<>();
        cardEntities.forEach(card->cards.add(card.getCardNumber()));
        return cards;
    }
}
