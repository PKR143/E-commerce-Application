package com.java.jwt.service;

import com.java.jwt.entity.TransactionStatusEntity;
import com.java.jwt.exception.PaymentException;
import com.java.jwt.repository.TransactionRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpGenerateService {

    @Autowired
    TransactionRepo txnRepo;

    private static final Logger logger = LoggerFactory.getLogger(OtpGenerateService.class);

    public String generateOtp(Long txnId){
        logger.info(txnId+" request to generate otp");

        if(txnId == null){
            logger.info("txn id can't be null to generate the otp");
            throw new PaymentException("Can't generate otp, because Txn id is missing");
        }

        Optional<TransactionStatusEntity> txnOpt = txnRepo.findById(txnId);
        if(txnOpt.isEmpty()){
            logger.info("txnId is not exist in db");
            throw new PaymentException("txnId not present in DB");
        }

        TransactionStatusEntity txnEntity = txnOpt.get();
        //check the transaction
        if (!txnEntity.getStatus().equalsIgnoreCase("INITIATED") || !(txnEntity.getStatusCode() == 0L)) {
            logger.info("error in txn status entity, status must be initiated and status code must be 0");
            throw new PaymentException("Transaction status must be initiated and status code must be 0");
        }

        Long random = 100000L + new Random().nextLong(900000L);
        logger.info(txnId+" otp generated successfully.");

        txnEntity.setOtp(String.valueOf(random));
        txnEntity.setOtpCreatedTime(String.valueOf(LocalTime.now().getMinute()));
        txnRepo.save(txnEntity);
        return String.valueOf(random);
    }

}
