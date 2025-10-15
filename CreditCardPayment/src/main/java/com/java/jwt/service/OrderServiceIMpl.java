package com.java.jwt.service;

import com.java.jwt.entity.*;
import com.java.jwt.exception.PaymentException;
import com.java.jwt.dto.*;
import com.java.jwt.repository.CardRepository;
import com.java.jwt.repository.CartRepository;
import com.java.jwt.repository.TransactionRepo;
import com.java.jwt.repository.UserRepository;
import com.java.jwt.util.TxnUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OrderServiceIMpl implements OrderService{


    @Autowired
    CardRepository cardRepo;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    UserRepository userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TransactionRepo txnRepo;

    @Autowired
    OtpGenerateService otpService;

    @Autowired
    MailService mailService;

    @Autowired
    SmsService smsService;


    private ExecutorService executorService = Executors.newFixedThreadPool(20);

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceIMpl.class);

    @Override
    public ResponseEntity<?> doPayment(TxnRequest request) throws PaymentException, RuntimeException {
//        return ResponseEntity.ok(new GeneralResponse(new OrderResponse("Payment API is Running"),1L,"OK"));

        logger.info("---------------payment request received---------------");
        try{

            logger.info("----------------validating txn request-------");

//            Optional<CardDetailsEntity> existCard = cardRepo.findById(request.getCardNumber());
            Optional<CardDetailsEntity> existCard = cardRepo.findByCardNumberAndUserName(request.getCardNumber(), request.getUsername());
            if(existCard.isEmpty()){
                logger.info("card is not registered");
                throw new PaymentException("Card is not registered");
            }
            CardDetailsEntity existCardEntity = existCard.get();
            String username = existCardEntity.getUsername();
            User exists = userRepo.findByUserName(username);
            if(exists == null){
                logger.info("requested username not exists in db");
                throw new PaymentException("Username not exists, please try with valid username");
            }

            //check card is active or not
            if(!existCardEntity.getIsActive()){
                logger.info("card is not active");
                throw new PaymentException("Card is not active!!!");
            }

            Optional<UserCartDetailsEntity> cartOpt = cartRepository.findById(username);
            if(cartOpt.isEmpty()){
                logger.info("Cart is empty, first add items to cart then place order");
                throw new PaymentException("Cart is empty, first add items to cart then place order");
            }

            UserCartDetailsEntity cartDetailsEntity = cartOpt.get();
            if(cartDetailsEntity.getItems().isEmpty()){
                logger.info("empty cart, add items in  the cart first.");
                throw new PaymentException("Cart is empty, first add items to cart then place order");
            }
//
//            //check the request amount
            Double requestAmount;
//            try{
//                requestAmount = Double.parseDouble(request.getRequestAmount());
//            }catch (Exception e){
//                logger.info("invalid amount received in request");
//                throw new PaymentException("invalid amount entered in request");
//            }

            //extracting the amount from the cart

            requestAmount = cartDetailsEntity.getAmount();

            //validating the requests

            if(!request.getCvv().equals(existCardEntity.getCvv())){
                logger.info("incorrect CVV provided in request");
                throw new PaymentException("incorrect CVV provided in request");
            }

            if(!request.getCardHolderName().equals(existCardEntity.getCardHolderName())){
                logger.info("incorrect Card Holder name provided in request");
                throw new PaymentException("incorrect Card Holder name provided in request");
            }

            if(!request.getExpiryYear().equals(existCardEntity.getExpiryYear())){
                logger.info("incorrect Expiry year provided in request");
                throw new PaymentException("incorrect Expiry year provided in request");
            }

            if(!request.getExpiryMonth().equals(existCardEntity.getExpiryMonth())){
                logger.info("incorrect Expiry Month provided in request");
                throw new PaymentException("incorrect Expiry Month provided in request");
            }

            if(!request.getMobileNo().equals(existCardEntity.getMobileNo())){
                logger.info("incorrect Mobile No provided in request");
                throw new PaymentException("incorrect MobileNo provided in request");
            }
            if(!request.getEmail().equals(existCardEntity.getMailId())){
                logger.info("incorrect mail id provided in request");
                throw new PaymentException("incorrect mail id provided in request");
            }

            if(requestAmount < 100 || requestAmount > existCardEntity.getCardLimit()){
                logger.info("amount range should be between 100 & {}",existCardEntity.getCardLimit());
                throw new PaymentException("amount range should be between 100 & "+existCardEntity.getCardLimit());
            }

            //generate txn id

            TransactionStatusEntity status = new TransactionStatusEntity();

            //check PIN
            String PIN = request.getPin();
            String actualPin = existCardEntity.getPin();

            if(!passwordEncoder.matches(PIN,actualPin)){
                logger.info(username+", "+request.getCardNumber()+", wrong PIN entered");
                throw new PaymentException("wrong PIN entered");
            }

            //initiate the transaction
            Long txnId = generateId();
            status.setTxnId(txnId);
            status.setUsername(username);
            status.setRequestAmount(requestAmount);
            status.setCardNumber(request.getCardNumber());
            status.setCvv(request.getCvv());
            status.setExpiryMonth(request.getExpiryMonth());
            status.setExpiryYear(request.getExpiryYear());
            status.setCardHolderName(request.getCardHolderName());
            status.setBankName(existCardEntity.getBankName());
            status.setIssuedMonth(existCardEntity.getIssuedMonth());
            status.setIssuedYear(existCardEntity.getIssuedYear());
            status.setPin(request.getPin());
            status.setIsActive(existCardEntity.getIsActive());
            status.setAddress(existCardEntity.getAddress());
            status.setMailId(existCardEntity.getMailId());
            status.setMobileNo(request.getMobileNo());
            status.setTransactionDate(new Date());

            status.setStatusCode(TxnUtil.INITIATED);
            status.setStatus("INITIATED");

            txnRepo.save(status);
            logger.info("transaction initiated with txn id: {}",txnId);
            logger.info("Generate otp for txnId: {}",txnId);

            String otp = otpService.generateOtp(txnId);

            executorService.submit(()->mailService.sendOtpInMail(otp, status));
//            executorService.submit(()->smsService.sendOtpInMobile(otp, status));

            return ResponseEntity.status(HttpStatus.OK).body(new GeneralResponse(new OtpResponse(txnId,"OTP generated successfully"), 1L, "OTP is valid for 10 minutes only."));
//            Double limit = existCardEntity.getCardLimit();
//            Double usedAmount = existCardEntity.getAmountUsed();
//            //processing transaction
//
//            if((requestAmount+usedAmount)  > limit){
//                logger.info(txnId+" Insufficient funds");
//                status.setStatusCode(TxnUtil.FAILED);
//                status.setStatus("FAILED");
//                txnRepo.save(status);
//                throw new PaymentException("Insufficient funds");
//            }
//
//            //settle the amount
//            existCardEntity.setAmountUsed(requestAmount+usedAmount);
//            cardRepo.save(existCardEntity);
//            logger.info(txnId+" transaction successful");
//            status.setStatusCode(TxnUtil.SUCCESS);
//            status.setStatus("SUCCESS");
//            txnRepo.save(status);
//
//
//
//            TxnResponse response = TxnResponse.builder()
//                    .txnId(txnId)
//                    .cardNumber(request.getCardNumber())
//                    .cardHolderName(request.getCardHolderName())
//                    .requestAmount(requestAmount)
//                    .cardType(CardUtil.findCardType(request.getCardNumber()))
//                    .mobileNo(request.getMobileNo())
//                    .build();
//            return ResponseEntity.ok(new GeneralResponse(response,1L,"Transaction Successful"));

        }catch(PaymentException e){
            logger.info("PaymentException @doPayment due to: {}",e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.info("Exception @doPayment due to: {}",e.getMessage());
            throw new PaymentException(e.getMessage());
        }



    }
    public Long generateId(){

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmssSS");
        String datetime = ft.format(dNow);
        return Long.parseLong(datetime);
    }

    @Override
    public ResponseEntity<?> checkOrder() {
        return ResponseEntity.ok(new GeneralResponse(new OrderResponse("Check Order API is Running"),1L,"OK"));
    }

    @Override
    public ResponseEntity<?> checkCart() {
        return ResponseEntity.ok(new GeneralResponse(new OrderResponse("Check Cart API is Running"),1L,"OK"));
    }

    @Override
    public ResponseEntity<?> addCard(CardRequest card) throws PaymentException {
        logger.info("----------------request received to add card--------");
        logger.info("request: {}",card.toString());
        try{
            logger.info("validating card details");

            if(isNullOrEmpty(card.getUsername())){
                logger.info("username is null");
                throw new PaymentException("username can't be null or empty");
            }
            User exists = userRepo.findByUserName(card.getUsername());
            if(exists == null){
                logger.info("username not exist in DB");
                throw new PaymentException("Username not exists, please try with valid username");
            }

            if(isNullOrEmpty(card.getCardNumber())){
                logger.info("card number can't be null or empty");
                throw new PaymentException("card number can't be empty or null");
            }

            Optional<CardDetailsEntity> existCard = cardRepo.findById(card.getCardNumber().trim());

            if(existCard.isPresent()){
                logger.info("card is already registered");
                throw new PaymentException("Duplicate card requested , please try with new card");
            }

            //check the card is expired or not

            if(Long.parseLong(card.getExpiryYear()) < LocalDate.now().getYear() && Long.parseLong(card.getExpiryMonth()) < LocalDate.now().getMonthValue()){
                logger.info("card is expired ");
                throw new PaymentException("Expired Card, please try with a valid card");
            }

            CardDetailsEntity entity = toEntity(card);
            cardRepo.save(entity);
            logger.info("----------------card details saved-------------------");
            return ResponseEntity.status(HttpStatus.CREATED).body(new GeneralResponse(new CardResponse(card.getCardNumber(), card.getCardHolderName()),1L, "Card registered successfully"));
        }catch(PaymentException e){
            logger.info("Order exception @addCard due to: {}",e.getMessage());
            throw e;
        }catch (Exception e){
            logger.info("Exception @addCard due to: {}",e.getMessage());
            throw new PaymentException(e.getMessage());
        }
    }

    public CardDetailsEntity toEntity(CardRequest request) {
        if (request == null) {
            return null;
        }
        return CardDetailsEntity.builder()
                .cardNumber(request.getCardNumber())
                .username(request.getUsername())
                .cvv(request.getCvv())
                .expiryMonth(request.getExpiryMonth())
                .expiryYear(request.getExpiryYear())
                .cardHolderName(request.getCardHolderName())
                .bankName(request.getBankName())
                .issuedMonth(request.getIssuedMonth())
                .issuedYear(request.getIssuedYear())
                .pin(request.getPin())
                .isActive(request.getIsActive())
                .cardLimit(request.getCardLimit())
                .amountUsed(request.getAmountUsed())
                .address(request.getAddress())
                .mailId(request.getMailId())
                .mobileNo(request.getMobileNo())
                .build();
    }

    private Boolean isNullOrEmpty(String o){
        return (o == null || o.trim().isEmpty());
    }
}
