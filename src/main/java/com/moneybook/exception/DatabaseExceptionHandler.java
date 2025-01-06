package com.moneybook.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class DatabaseExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseExceptionHandler.class);
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        logger.warn("Constraint violation: {}", ex.getMostSpecificCause().getMessage());
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                "A conflict occurred due to duplicate or invalid data.",
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorDetails> handleDataAccessException(DataAccessException ex, WebRequest request) {
        String message = "Database access error: " + ex.getMostSpecificCause().getMessage();
        ErrorDetails errorDetails = new ErrorDetails(new Date(),
                "An unexpected error occurred. Please contact support.",
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
