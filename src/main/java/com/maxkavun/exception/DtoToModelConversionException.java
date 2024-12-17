package com.maxkavun.exception;

public class DtoToModelConversionException extends ApplicationException{

    public DtoToModelConversionException(String message) {
        super(message);
    }

    public DtoToModelConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
