package com.milko.user_provider.exceptions;

public class FieldsNotFilledException extends RuntimeException{
    public FieldsNotFilledException(String message) {
        super(message);
    }
}
