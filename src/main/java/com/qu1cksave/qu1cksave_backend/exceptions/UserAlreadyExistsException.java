package com.qu1cksave.qu1cksave_backend.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    public UserAlreadyExistsException(String message, Throwable err) {
        super(message, err);
    }
}
