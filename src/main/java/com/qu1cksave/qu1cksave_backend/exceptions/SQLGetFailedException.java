package com.qu1cksave.qu1cksave_backend.exceptions;

// Used if a select fails for some unknown reason
// - Not really totally unknown. But this IS NOT used if the job can't be found
public class SQLGetFailedException extends RuntimeException {
    public SQLGetFailedException(String message) {
        super(message);
    }
    public SQLGetFailedException(String message, Throwable err) {
        super(message, err);
    }
}
