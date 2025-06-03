package com.qu1cksave.qu1cksave_backend.exceptions;

// Used if for example a job can't be found, but it is expected to be found
// Ex. In delete job, the job is retrieved first before deleting
// - If job does not exist, should throw this exception
// DON'T USE FOR: getOneJob, where job might not exist. There, it's expected
//   that the job may not exist so just return null and adjust the status
//   code as appropriate.
public class SQLNotFoundException extends RuntimeException {
    public SQLNotFoundException(String message) {
        super(message);
    }
    public SQLNotFoundException(String message, Throwable err) {
        super(message, err);
    }
}
