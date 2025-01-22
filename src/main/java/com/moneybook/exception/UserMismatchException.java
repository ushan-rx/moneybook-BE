package com.moneybook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserMismatchException extends Exception {
    public UserMismatchException(String message) {
        super(message);
    }
}
