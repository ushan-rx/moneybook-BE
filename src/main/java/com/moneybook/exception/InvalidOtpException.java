package com.moneybook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidOtpException extends Exception {
    public InvalidOtpException(String message) {
        super(message);
    }
}
