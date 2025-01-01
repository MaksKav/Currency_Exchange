package com.maxkavun.exception;

public class CurrencyNotFoundException extends ApplicationException{
    public CurrencyNotFoundException(String message) {
        super(message);
    }

    public CurrencyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
