package com.maxkavun.exception;

public class ExchangeRateAlreadyExistsException extends ApplicationException{
    public ExchangeRateAlreadyExistsException(String message) {
        super(message);
    }

    public ExchangeRateAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
