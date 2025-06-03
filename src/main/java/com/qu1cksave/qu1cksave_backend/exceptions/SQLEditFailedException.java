package com.qu1cksave.qu1cksave_backend.exceptions;

public class SQLEditFailedException extends RuntimeException {
    public SQLEditFailedException(String message) {
        super(message);
    }
    public SQLEditFailedException(String message, Throwable err) {
      super(message, err);
    }
}
