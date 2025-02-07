package com.maxkavun.validator;

public class ExchangeCurrenciesValidator {

    public static boolean isCurrenciesCodeValid(String baseCurrencyCode, String targetCurrencyCode) {
        return baseCurrencyCode != null && targetCurrencyCode != null &&
               baseCurrencyCode.length() == 3 && targetCurrencyCode.length() == 3 &&
               baseCurrencyCode.matches("[A-Z]+") && targetCurrencyCode.matches("[A-Z]+") &&
               !baseCurrencyCode.equals(targetCurrencyCode);
    }
}
