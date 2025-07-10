package com.qu1cksave.qu1cksave_backend.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

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
    public Object handleSQLExceptions(HttpServletRequest req, Exception ex) {
//        printErr(ex); // TODO: Comment out in production
        return null;
    }

    // For example, when doing editing or deleting a job and the job
    //   or any of its files aren't found
    @ExceptionHandler(SQLNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleSQLNotFoundException(HttpServletRequest req, Exception ex) {
//        printErr(ex); // TODO: Comment out in production
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
    public Object handleForbiddenResourceException(HttpServletRequest req, Exception ex) {
//        printErr(ex); // TODO: Comment out in production
        return null;
    }

    // No user id query provided for get multiple jobs
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMissingServletRequestParameterException(HttpServletRequest req, Exception ex) {
//        printErr(ex); // TODO: Comment out in production
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
    public Object handleStaleFrontendJobException(HttpServletRequest req, Exception ex) {
//        printErr(ex); // TODO: Comment out in production
        return null;
    }

    @ExceptionHandler({
        S3DeleteFailedException.class,
        S3GetFailedException.class,
        S3PutFailedException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // TODO: Is 500 appropriate?
    public Object handleS3Exceptions(HttpServletRequest req, Exception ex) {
//        printErr(ex); // TODO: Comment out in production
        return null;
    }

    // TODO: Need to include exceptions for when a bad request body is included
    //  - Missing required fields
    //  - Extra fields
    //  - Fields with wrong type

    // https://stackoverflow.com/questions/17201072/using-spring-mvc-accepting-post-requests-with-bad-json-leads-to-a-default-400-e
    // - If bad request (wrong types, etc.) is received, this is supposed to
    //   be called APPARENTLY (NEED TO TEST)
    // https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
    // - To access the exception
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Object handleBadRequestBody() {
    public Object handleBadRequestBodyExceptions(HttpServletRequest req, Exception ex) {
//        printErr(ex); // TODO: Comment out in production
        return null;
    }

    // Note: I'm using a 404 Not Found Exception to not give away the existence
    //   of a user
    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleInvalidCredentialsException(HttpServletRequest req, Exception ex) {
//        printErr(ex); // TODO: Comment out in production
        return null;
    }

    // TODO: I read that stating that a user already exists is a security
    //   issue. What status code to show instead?
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
//    public Object handleUserAlreadyExistsException() {
    public Object handleUserAlreadyExistsException(HttpServletRequest req, Exception ex) {
//        printErr(ex); // TODO: Comment out in production
        return null;
    }

    @ExceptionHandler({
        RuntimeException.class,
        Exception.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public Object handleOtherExceptions() {
    public Object handleOtherExceptions(HttpServletRequest req, Exception ex) {
//        printErr(ex); // TODO: Comment out in production
        return null;
    }

    // TODO: Uncomment calls to this in production
    //  - Also use an actual logging system
    public void printErr(Exception ex) {
        System.out.println("****** STACK TRACE: " + Arrays.toString(ex.getStackTrace()));
        System.out.println("****** MESSAGE: " + ex.getMessage());
//        System.out.println("****** CAUSE:" + ex.getCause().toString());
    }

    // TODO: Add a handler for Exception and RuntimeException
    //  - Does most general go to the bottom?
    //  - https://stackoverflow.com/questions/19498378/setting-precedence-of-multiple-controlleradvice-exceptionhandlers
}

// Useful resources:
// - https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
// - https://www.baeldung.com/exception-handling-for-rest-with-spring
// - https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html
