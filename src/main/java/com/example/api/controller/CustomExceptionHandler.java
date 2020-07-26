package com.example.api.controller;

import com.example.api.exception.ValidationException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {


//    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> handleSqlException(Exception exception, WebRequest webRequest) {
        exception = new ValidationException("Entity validation error, please recheck the body of request.", exception);
        return handleExceptionInternal(exception, getErrorBody(exception, BAD_REQUEST, webRequest),
                new HttpHeaders(), BAD_REQUEST, webRequest);
    }

    @ExceptionHandler({InvalidDataAccessApiUsageException.class})
    public ResponseEntity<?> handleIncorrectSearchParameter(Exception exception, WebRequest request) {
        exception = new ValidationException("Property not found. " + exception.getMessage(), exception);
        return handleExceptionInternal(exception, getErrorBody(exception, BAD_REQUEST, request),
                new HttpHeaders(), BAD_REQUEST, request);
    }

    @Value
    public static class ErrorResponseBody {
        Timestamp timestamp = Timestamp.from(Instant.now(Clock.systemDefaultZone()));
        int status;
        String message;
        String exception;
        String path;
    }

    private ErrorResponseBody getErrorBody(Exception e, HttpStatus status, WebRequest request) {
        return new ErrorResponseBody(status.value(), e.getMessage(), e.getClass().getName(),
                ((ServletWebRequest) request).getRequest().getRequestURI());
    }
}
