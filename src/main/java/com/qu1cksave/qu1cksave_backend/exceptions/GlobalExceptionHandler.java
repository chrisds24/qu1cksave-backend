package com.qu1cksave.qu1cksave_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // TODO: (6/4/25) Have these return a custom error object later
    //  - Will need to fix frontend to accept this error object

    // These custom SQL exceptions happen when there's an unexpected
    //   error when making a database query that is not due to
    //   bad user input
    @ExceptionHandler({
        SQLAddFailedException.class,
        SQLDeleteFailedException.class,
        SQLEditFailedException.class,
        SQLGetFailedException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleSQLExceptions() {
        return null;
    }

    // For example, when doing editing or deleting a job and the job
    //   or any of its files aren't found
    @ExceptionHandler(SQLNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleSQLNotFoundException() {
        return null;
    }

    // When a user tries to access a resource that they should not be able
    //   to access, such as a job that doesn't belong to them or a list of
    //   jobs that they don't own (by providing a different user id in the
    //   query params compared to the one in the auth header)
    // I'm returning 404 Not Found to not give out extra info that could be
    //   useful for attackers
    @ExceptionHandler(ForbiddenResourceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleForbiddenResourceException() {
        return null;
    }

    // When a user tries to update a stale/outdated job
    // - Here, "stale" means the job has a different resume id and/or cover
    //   letter id (which could cause problems)
    // - Other job info being outdated isn't considered "stale"
    // https://stackoverflow.com/questions/38681257/what-http-status-code-is-appropriate-when-a-client-attempts-to-update-a-data-ent
    // - 409 is the suggested status code for updating outdated data
    @ExceptionHandler(StaleFrontendJobException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Object handleStaleFrontendJobException() {
        return null;
    }

    @ExceptionHandler({
        S3DeleteFailedException.class,
        S3GetFailedException.class,
        S3PutFailedException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // TODO: Is 500 appropriate?
    public Object handleS3Exceptions() {
        return null;
    }

    // TODO: Add a handler for Exception and RuntimeException
    //  - Does most general go to the bottom?
}

// Useful resources:
// - https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
// - https://www.baeldung.com/exception-handling-for-rest-with-spring
// - https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html
