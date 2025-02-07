package com.maxkavun.validator;

import java.math.BigDecimal;

public class RateAmountValidator {

    public static  boolean isValidRate(String rateStr) {
        BigDecimal rate;
        try {
            rate = new BigDecimal(rateStr);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return rate.compareTo(BigDecimal.ZERO) > 0;
    }
}
