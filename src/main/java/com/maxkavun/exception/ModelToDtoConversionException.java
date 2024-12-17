package com.maxkavun.exception;

public class ModelToDtoConversionException extends ApplicationException{

    public ModelToDtoConversionException(String message) {
        super(message);
    }

    public ModelToDtoConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
