package com.maxkavun.dto;

public class ExchangeConvertErrorResponse {

    private final String message;

    public ExchangeConvertErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
