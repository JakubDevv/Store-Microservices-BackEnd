package com.example.store.controller;

import com.example.store.exception.*;
import com.example.store.exception.filter.FilterNotFoundException;
import com.example.store.exception.filtervalue.FilterValueNotFoundException;
import com.example.store.exception.maincategory.MainCategoryNotFoundException;
import com.example.store.exception.order.OrderNotFoundException;
import com.example.store.exception.product.ProductNotFoundException;
import com.example.store.exception.product.SizeNotFoundException;
import com.example.store.exception.subcategory.SubcategoryNotFoundException;
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
            FilterNotFoundException.class,
            FilterValueNotFoundException.class,
            MainCategoryNotFoundException.class,
            OrderNotFoundException.class,
            ProductNotFoundException.class,
            SizeNotFoundException.class,
            SubcategoryNotFoundException.class,
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
