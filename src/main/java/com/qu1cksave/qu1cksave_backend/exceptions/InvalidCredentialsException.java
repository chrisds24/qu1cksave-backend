package com.qu1cksave.qu1cksave_backend.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
    public InvalidCredentialsException(String message, Throwable err) {
        super(message, err);
    }
}
