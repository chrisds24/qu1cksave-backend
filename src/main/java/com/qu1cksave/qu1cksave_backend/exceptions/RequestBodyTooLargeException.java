package com.qu1cksave.qu1cksave_backend.exceptions;

public class RequestBodyTooLargeException extends RuntimeException {
    public RequestBodyTooLargeException(String message) {
        super(message);
    }
    public RequestBodyTooLargeException(String message, Throwable err) {
        super(message, err);
    }
}
