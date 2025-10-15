package com.java.jwt.service;


import com.java.jwt.dto.*;
import com.java.jwt.entity.CardDetailsEntity;
import com.java.jwt.entity.ProductDetailsEntity;
import com.java.jwt.entity.UserCartDetailsEntity;
import com.java.jwt.repository.CartRepository;
import com.java.jwt.util.CardUtil;
import com.java.jwt.entity.TransactionStatusEntity;
import com.java.jwt.exception.PaymentException;
import com.java.jwt.repository.CardRepository;
import com.java.jwt.repository.TransactionRepo;
import com.java.jwt.util.TxnUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CompleteTransactionService {

    @Autowired
    TransactionRepo txnRepo;

    @Autowired
    CardRepository cardRepo;

    @Autowired
    CartRepository cartRepo;

    @Autowired
    MailService mailService;

    @Autowired
    SmsService smsService;

    @Autowired
    PubSubService pubSubService;

    private ExecutorService executorService = Executors.newFixedThreadPool(20);

    private static final Logger logger = LoggerFactory.getLogger(CompleteTransactionService.class);

    public ResponseEntity<?> completeTxn(Long txnId, String otp){
        try {

            if (txnId == null) {
                logger.info("txnId is required to complete the transaction");
                throw new PaymentException("txnId is required to complete the transaction");
            }
            logger.info(txnId + " request to complete the transaction");
            //validating the txn id
            Optional<TransactionStatusEntity> optTxn = txnRepo.findById(txnId);
            if (optTxn.isEmpty()) {
                logger.info(txnId + " is invalid, not exist in db");
                throw new PaymentException("txn id is not exist in db");
            }

            TransactionStatusEntity txnEntity = optTxn.get();

            if (txnEntity.getStatus().equalsIgnoreCase("SUCCESS") || (txnEntity.getStatusCode() == 1L)) {
                logger.info("Txn is already successful");
                throw new PaymentException("Transaction is already Successful!!");
            }

            if (!txnEntity.getStatus().equalsIgnoreCase("INITIATED") || !(txnEntity.getStatusCode() == 0L)) {
                logger.info("error in txn status entity, status must be initiated and status code must be 0");
                throw new PaymentException("Transaction status must be initiated and status code must be 0");
            }

            Optional<UserCartDetailsEntity> cartOpt = cartRepo.findById(txnEntity.getUsername());

            //validate the otp
            if (otp == null || otp.isEmpty()) {
                logger.info("otp can't be null or empty");
                throw new PaymentException("Otp can't be null or empty in the request");
            }

            if (!otp.trim().equals(txnEntity.getOtp())) {
                logger.info(otp + " incorrect otp entered");
                throw new PaymentException("otp mismatch, please provide correct otp");
            }

            if ((LocalTime.now().getMinute() - Long.parseLong(txnEntity.getOtpCreatedTime()) >= 10) && (LocalTime.now().getMinute() - Long.parseLong(txnEntity.getOtpCreatedTime()) < 0 )){
                logger.info(otp + " is expired, otp is valid only for 10 minutes");
                throw new PaymentException("otp is valid only for 10 minutes, generate a new otp to complete the transaction");
            }

            //complete the transaction

            Optional<CardDetailsEntity> existCard = cardRepo.findById(txnEntity.getCardNumber());

            if (existCard.isEmpty()) {
                logger.info("unable to find card details from db");
                throw new PaymentException("card number not found in db");
            }
            CardDetailsEntity existCardEntity = existCard.get();
            Double limit = existCardEntity.getCardLimit();
            Double usedAmount = existCardEntity.getAmountUsed();
            Double requestAmount = txnEntity.getRequestAmount();
            //processing transaction

            if ((requestAmount + usedAmount) > limit) {
                logger.info(txnId + " Maximum amount limit exceed");
                txnEntity.setStatusCode(TxnUtil.FAILED);
                txnEntity.setStatus("FAILED");
                txnRepo.save(txnEntity);
                throw new PaymentException("Maximum amount limit exceed");
            }

            //settle the amount
            existCardEntity.setAmountUsed(requestAmount + usedAmount);
            cardRepo.save(existCardEntity);
            logger.info(txnId + " transaction successful");
            txnEntity.setStatusCode(TxnUtil.SUCCESS);
            txnEntity.setStatus("SUCCESS");
            txnEntity.setTransactionDate(new Date());
            txnRepo.save(txnEntity);


            if(cartOpt.isEmpty()){
                logger.info("Cart details is not available fro the username");
                throw new PaymentException("Something went wrong please try again after sometimes");

            }
            UserCartDetailsEntity cartDetailsEntity = cartOpt.get();
            //set transaction event
            TransactionEvent event = mapToTransactionEvent(txnEntity, cartDetailsEntity.getItems());

            executorService.submit(()-> {
                try {
                    pubSubService.publishMessage(event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            //make the cart empty and
            CartDetails cartDetails = mapToCartDetails(cartDetailsEntity);
            cartDetailsEntity.setAmount(0D);
            cartDetailsEntity.setItems(new ArrayList<>());
            cartRepo.save(cartDetailsEntity);

            TxnResponse response = TxnResponse.builder()
                    .txnId(txnId)
                    .cardNumber(txnEntity.getCardNumber())
                    .cardHolderName(txnEntity.getCardHolderName())
                    .requestAmount(requestAmount)
                    .cardType(CardUtil.findCardType(txnEntity.getCardNumber()))
                    .mobileNo(txnEntity.getMobileNo())
                    .build();


            executorService.submit(()->mailService.sendTxnMail(cartDetails, response , txnEntity));
//            mailService.sendTxnMail(cartDetails, response , txnEntity);
//            executorService.submit(()-> smsService.sendAck(txnEntity));
            logger.info("UserCartDetailsEntity @completeTxn: {}",cartOpt.get().getUsername());
            logger.info(txnId+" Transaction Successful!");
            return ResponseEntity.ok(new GeneralResponse(response, 1L, "Transaction Successful"));

        } catch (Exception e) {
            logger.info("Exception occurred while completing the transaction");
            throw e;
        }

    }
    private TransactionEvent mapToTransactionEvent(TransactionStatusEntity status, List<ProductDetailsEntity> items) {
        return TransactionEvent.builder()
                .txnId(status.getTxnId())
                .username(status.getUsername())
                .items(items)
                .amount(status.getRequestAmount())
                .mobileNum(status.getMobileNo())
                .mailId(status.getMailId())
                .orderDate(status.getTransactionDate())
                .address(status.getAddress())
                .status(status.getStatus())
                .statusCode((long) status.getStatusCode())
                .build();
    }

    private CartDetails mapToCartDetails(UserCartDetailsEntity cartDetailsEntity) {
        return CartDetails.builder()
                .username(cartDetailsEntity.getUsername())
                .items(mapToProductDetails(cartDetailsEntity.getItems()))
                .amount(cartDetailsEntity.getAmount())
                .build();
    }

    private List<ProductDetails> mapToProductDetails(List<ProductDetailsEntity> items) {

        ProductDetails productDetails;
        List<ProductDetails> set = new ArrayList<>();
        for(ProductDetailsEntity p : items){
            productDetails = ProductDetails.builder()
                    .productName(p.getProductName())
                    .productType(p.getProductType())
                    .price(p.getPrice())
                    .build();
            set.add(productDetails);
        }
        return set;
    }


}
