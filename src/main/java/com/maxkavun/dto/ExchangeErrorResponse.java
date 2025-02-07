package com.maxkavun.dto;

public class ExchangeErrorResponse {

    private final String message;

    public ExchangeErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
