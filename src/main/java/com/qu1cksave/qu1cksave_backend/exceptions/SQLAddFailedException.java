package com.qu1cksave.qu1cksave_backend.exceptions;

public class SQLAddFailedException extends RuntimeException {
    public SQLAddFailedException(String message) {
        super(message);
    }
    public SQLAddFailedException(String message, Throwable err) {
        super(message, err);
    }
}
