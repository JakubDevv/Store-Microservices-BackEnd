package com.example.identity.controller;

import com.example.identity.exceptions.CompanyNotFoundException;
import com.example.identity.exceptions.ErrorMessage;
import com.example.identity.exceptions.UserNotFoundException;
import com.example.identity.exceptions.ValidationErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ValidationErrorMessage> validationErrorException(MethodArgumentNotValidException e) {
        List<String> details = getAllErrorsDetails(e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ValidationErrorMessage(
                        HttpStatus.CONFLICT.toString(),
                        "Validation error, check details",
                        LocalDateTime.now(),
                        details
                ));
    }

    @ExceptionHandler({
            CompanyNotFoundException.class,
            UserNotFoundException.class,
    })
    public ResponseEntity<ErrorMessage> notFoundException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(
                        HttpStatus.NOT_FOUND.toString(),
                        e.getMessage(),
                        LocalDateTime.now()));
    }

    private List<String> getAllErrorsDetails(MethodArgumentNotValidException e) {
        List<String> details = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            details.add(fieldName + ": " + errorMessage);
        });
        return details;
    }
}
