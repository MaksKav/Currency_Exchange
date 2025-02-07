package com.maxkavun.validator;

public class CurrencyValidator {

    public static boolean isValidCurrencieData(String fullName , String code , String sign) {
        int MAX_NAME_LENGTH = 50;
        int MAX_SIGN_LENGTH = 3;

        return fullName != null && !fullName.trim().isEmpty() &&
               code != null && !code.trim().isEmpty() &&
               sign != null && !sign.trim().isEmpty() &&
               code.matches("^[A-Za-z]{3}$") &&
               fullName.length() <= MAX_NAME_LENGTH &&
               sign.length() <= MAX_SIGN_LENGTH;

    }
}
