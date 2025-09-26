package com.java.jwt.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;


public class CardUtil {

    private static final Logger logger = LoggerFactory.getLogger(CardUtil.class);

    public static String findCardType(String cardNum) {
        String cardType = null;

        if (cardNum == null || cardNum.isEmpty()) {
            logger.info("Card number can't be null or empty");
            return null;
        }

        // Visa: starts with 4 and 16 digits total
        Pattern visa   = Pattern.compile("^4\\d{15}$");

        // RuPay: starts with 60, 65, 81, or 82 and 16 digits total
        Pattern rupay  = Pattern.compile("^(60|65|81|82)\\d{14}$");

        // MasterCard: 16 digits, prefix 51–55 or 2221–2720
        Pattern mc     = Pattern.compile("^(5[1-5]\\d{14}|2(2[2-9]\\d{12}|[3-6]\\d{13}|7[01]\\d{12}|720\\d{12}))$");

        if (visa.matcher(cardNum).matches()) {
            cardType = "VISA";
        } else if (mc.matcher(cardNum).matches()) {
            cardType = "MASTERCARD";
        } else if (rupay.matcher(cardNum).matches()) {
            cardType = "RUPAY";
        } else {
            cardType = "UNKNOWN";
        }

        return cardType;
    }


}
