package com.qu1cksave.qu1cksave_backend.exceptions;

public class S3PutFailedException extends RuntimeException {
    public S3PutFailedException(String message) {
        super(message);
    }
    public S3PutFailedException(String message, Throwable err) {
        super(message, err);
    }
}
