package com.example.cards.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomHttpException extends RuntimeException {

    private final HttpStatus status;
    private final String reason;


    public CustomHttpException( HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.reason = message;
    }
}

