package com.java.jwt.service;

import com.java.jwt.entity.TransactionStatusEntity;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class SmsService {


    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;


    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    public void sendSms(String toPhoneNumber, String messageBody) {
        try {
            logger.info("send sms request initiated");
            Twilio.init(accountSid, authToken);
            Message message = Message.creator(
                            new PhoneNumber(toPhoneNumber),
                            new PhoneNumber(twilioPhoneNumber),
                            messageBody)
                    .create();
            System.out.println("SMS sent with SID: " + message.getSid());
        } catch (Exception e) {
            logger.info("Exception while sending sms to "+toPhoneNumber);
            throw new RuntimeException(e);
        }
    }
    public void sendOtpInMobile(String otp, TransactionStatusEntity txnEntity) {

        logger.info(txnEntity.getTxnId()+" sending otp to mobile number");
        sendSms(txnEntity.getMobileNo(), generateMessage(otp));
        logger.info(txnEntity.getTxnId()+" otp sent to mobile number successfully");

    }

    public void sendAck(TransactionStatusEntity txnEntity){
        logger.info(txnEntity.getTxnId()+" sending transaction response to mobile number");
        sendSms(txnEntity.getMobileNo(), generateSms(txnEntity));
    }

    public String generateMessage(String otp) {
        return String.format(
                otp+" is your OTP to complete the transaction.%n"+
                        "It is valid only for 10 minutes.%n"+
                        "Don't share your OTP with anyone."
        );
    }
    public String generateSms(TransactionStatusEntity txnEntity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        String formattedDateTime = LocalDate.now().format(formatter);

        return String.format(
                "Dear %s,%n%nYour payment of \u20B9%.2f was successful.%nTxn ID: %s%nMobile: %s%nDate & Time: %s%n%nIf you did not authorize this transaction, please contact us immediately.%n%nThank you for choosing us.%n- Your Company Name",
                txnEntity.getCardHolderName(), txnEntity.getRequestAmount(), txnEntity.getTxnId(), txnEntity.getMobileNo(), formattedDateTime
        );
    }

}
