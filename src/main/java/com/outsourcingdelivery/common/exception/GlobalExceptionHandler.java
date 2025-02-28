package com.outsourcingdelivery.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Map<String, String>> handleBaseException(BaseException ex) {
        Map<String, String> test = new HashMap<>();
        test.put("errorCode" , String.valueOf(ex.getErrorCode()));
        test.put("message ", ex.getMessage());

        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(test);
    }
}
