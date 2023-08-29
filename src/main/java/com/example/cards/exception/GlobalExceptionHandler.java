package com.example.cards.exception;

import com.example.cards.response.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLSyntaxErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<GenericResponse> handleExpiredJwtException(JwtExpiredException ex) {
        String message = "JWT expired at " + ex.getMessage();
        GenericResponse errorResponse = GenericResponse.builder().data(null)
                .message(message).success(false).status(HttpStatus.FORBIDDEN.value()).build();
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomException.class)
    public GenericResponse handleCustomException(CustomException ex) {
        String errorMessage = ex.getLocalizedMessage();
        GenericResponse response = new GenericResponse();
        response.setMessage(errorMessage);
        response.setData(null);
        response.setSuccess(false);
        response.setStatus(400);
        return response;
    }

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public GenericResponse handleSqlSyntaxException(SQLSyntaxErrorException ex) {
        String errorMessage = ex.getLocalizedMessage();
        GenericResponse response = new GenericResponse();
        response.setMessage(errorMessage);
        response.setData(null);
        response.setSuccess(false);
        response.setStatus(400);
        return response;
    }

}
