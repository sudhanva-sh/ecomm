package com.ecommerce.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFound(ResourceNotFoundException ex){

        return new ResponseEntity<>( ExceptionResponse.builder().message(ex.getMessage()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex){
        return new ResponseEntity<>(ExceptionResponse.builder().message(ex.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
