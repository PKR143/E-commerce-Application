package com.java.jwt.service;

import com.java.jwt.dto.*;
import com.java.jwt.entity.ProductDetailsEntity;
import com.java.jwt.entity.TransactionStatusEntity;
import com.java.jwt.entity.UserCartDetailsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MailService {

    @Autowired
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String from;

    private ExecutorService executorService = Executors.newFixedThreadPool(20);
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    public void sendOtpInMail(String otp, TransactionStatusEntity txnEntity){
        logger.info(txnEntity.getTxnId()+" sending otp to the mailId");
        MailRequest request = MailRequest.builder()
                .to(txnEntity.getMailId())
                .subject("OTP for Transaction")
                .body(String.format(
                        "Hello %s,%n%n" +
                                "Here is your otp which is valid for 10 minutes only.%n" +
                                "OTP: %s%n%n" +
                                "For amount : %s%n" +
                                "Don't share your OTP with anyone%n"+
                                "Mobile Number  : %s%n%n" +
                                "Best regards,%niServeU pvt. ltd.",
                        txnEntity.getCardHolderName(),
                        otp,
                        txnEntity.getRequestAmount(),
                        txnEntity.getMobileNo()
                ))
                .build();
        sendMail(request);
        logger.info(txnEntity.getTxnId()+" otp sent to mailId successfully");
    }

    public MailResponse sendMail(MailRequest request) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        try {

            simpleMailMessage.setFrom(from);
            simpleMailMessage.setTo(request.getTo());
            simpleMailMessage.setSubject(request.getSubject());
            simpleMailMessage.setText(request.getBody());

            mailSender.send(simpleMailMessage);

            return MailResponse.builder()
                    .status(1L)
                    .statusDesc("mail sent successfully!")
                    .build();
        } catch (Exception e) {
            logger.info("exception @mail service due to: {}",e.getMessage());
            return MailResponse.builder()
                    .status(-1L)
                    .statusDesc("something went wrong!!")
                    .build();
        }
    }

    public void sendTxnMail(CartDetails cartDetailsEntity, TxnResponse response, TransactionStatusEntity txnEntity){
//        logger.info("UserCartDetailsEntity @ sendTxnMail: {}",cartDetailsEntity.getItems());
        MailRequest mailRequest = MailRequest.builder()
                .to(txnEntity.getMailId())
                .subject("Transaction Status")
                .body(generateInvoiceMessage(cartDetailsEntity.getItems(), response, "Transaction Successful!!"))
                .build();
        MailResponse response1 = sendMail(mailRequest);
        if(response1.getStatus() == -1L){
            logger.info("error while sending mail due to: {}",response1.getStatusDesc());
        }else{
            logger.info("successfully sent the mail");
        }
    }

    private String generateMessage(Set<ProductDetails> cartDetailsEntity, TxnResponse txn, String statusDesc) {

        return String.format(
                "Hello %s,%n%n" +
                        "Thank you for your transaction.%n" +
                        "Here are your details:%n" +
                        "Transaction ID : %s%n" +
                        "Amount         : %s%n" +
                        "Mobile Number  : %s%n%n" +
                        "Message: %s%n%n" +
                        "Best regards,%niServeU pvt. ltd.",
                txn.getCardHolderName(),
                txn.getTxnId(),
                txn.getRequestAmount(),
                txn.getMobileNo(),
                statusDesc
        );



    }


//
//    private String generateMessage2(Set<ProductDetailsEntity> productDetailsEntity, TxnResponse response, String statusDesc){
//
//    }

    private String generateInvoiceMessage(List<ProductDetails> cartDetailsEntity,
                                          TxnResponse txn,
                                          String statusDesc) {

        // Build product table
        StringBuilder productLines = new StringBuilder();
        Double totalPrice = 0.0;
        int slNo = 1;

        for (ProductDetails p : cartDetailsEntity) {
            productLines.append(String.format(
                    "%-5d %-25s %-20s %10.2f%n",
                    slNo++,
                    p.getProductName(),
                    p.getProductType(),
                    p.getPrice()
            ));
            totalPrice += p.getPrice();
        }
logger.info("Product lines: {}",productLines);
        // Compose invoice
        return String.format(
                "Hello %s,%n%n" +
                        "Thank you for your order. Below is your invoice:%n%n" +
                        "Transaction Details:%n" +
                        "  Transaction ID : %s%n" +
                        "  Amount Paid    : %.2f%n" +
                        "  Mobile Number  : %s%n" +
                        "  Status         : %s%n%n" +
                        "Order Items:%n" +
                        "SlNo  Product Name              Product Type          Price%n" +
                        "---------------------------------------------------------------%n" +
                        "%s" +
                        "---------------------------------------------------------------%n" +
                        "Total Price: %.2f%n%n" +
                        "Best regards,%n" +
                        "iServeU Pvt. Ltd.",
                txn.getCardHolderName(),
                txn.getTxnId(),
                txn.getRequestAmount(),
                txn.getMobileNo(),
                statusDesc,
                productLines,
                totalPrice
        );
    }



}
