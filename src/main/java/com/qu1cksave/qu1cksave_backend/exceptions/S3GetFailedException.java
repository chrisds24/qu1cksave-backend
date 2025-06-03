package com.qu1cksave.qu1cksave_backend.exceptions;

public class S3GetFailedException extends RuntimeException {
    public S3GetFailedException(String message) {
        super(message);
    }
    public S3GetFailedException(String message, Throwable err) {
        super(message, err);
    }
}
