package com.maxkavun.exception;

public class BusinessException  extends  ApplicationException{

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
