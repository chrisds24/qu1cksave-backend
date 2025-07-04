package com.qu1cksave.qu1cksave_backend.exceptions;

public class CustomFilterException extends RuntimeException {
    public CustomFilterException(String message) {
        super(message);
    }
    public CustomFilterException(String message, Throwable err) {
        super(message, err);
    }
}
