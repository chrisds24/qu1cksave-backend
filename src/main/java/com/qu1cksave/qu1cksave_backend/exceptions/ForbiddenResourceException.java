package com.qu1cksave.qu1cksave_backend.exceptions;

public class ForbiddenResourceException extends RuntimeException {
    public ForbiddenResourceException(String message) {
        super(message);
    }
    public ForbiddenResourceException(String message, Throwable err) {
        super(message, err);
    }
}
