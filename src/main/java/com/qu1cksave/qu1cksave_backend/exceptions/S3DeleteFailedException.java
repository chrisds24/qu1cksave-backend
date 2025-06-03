package com.qu1cksave.qu1cksave_backend.exceptions;

public class S3DeleteFailedException extends RuntimeException {
    public S3DeleteFailedException(String message) {
        super(message);
    }
    public S3DeleteFailedException(String message, Throwable err) {
        super(message, err);
    }
}
