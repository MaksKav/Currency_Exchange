package com.maxkavun.validator;

public class CurrencyValidator {
    public static boolean validateCurrencyData(String fullName , String code , String sign) {
        return fullName != null && !fullName.trim().isEmpty() &&
               code != null && !code.trim().isEmpty() &&
               sign != null && !sign.trim().isEmpty() &&
               code.matches("^[A-Za-z]{3}$");
    }
}
