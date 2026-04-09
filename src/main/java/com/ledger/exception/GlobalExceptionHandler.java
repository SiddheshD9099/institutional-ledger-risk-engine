package com.ledger.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleAppException(AppException e) {

        return ResponseEntity
                .status(e.getStatus())
                .body(Map.of(
                        "status", "error",
                        "message", e.getMessage()
                ));
    }

}