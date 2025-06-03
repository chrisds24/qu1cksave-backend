package com.qu1cksave.qu1cksave_backend.exceptions;

public class StaleFrontendJobException extends RuntimeException {
    public StaleFrontendJobException(String message) {
        super(message);
    }
    public StaleFrontendJobException(String message, Throwable err) {
      super(message, err);
  }
}
