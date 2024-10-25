package com.SWP391.KoiXpress.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidation(MethodArgumentNotValidException exception){
        String message = "";
        for(FieldError fieldError: exception.getBindingResult().getFieldErrors()){
            message += fieldError.getDefaultMessage() +"\n";
        }
        //thanh cong nhung tra ve dang bad_request
        return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleValidation(Exception exception){

        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
