package com.qu1cksave.qu1cksave_backend.exceptions;

public class SQLDeleteFailedException extends RuntimeException {
    public SQLDeleteFailedException(String message) {
        super(message);
    }
    public SQLDeleteFailedException(String message, Throwable err) {
        super(message, err);
    }
}
