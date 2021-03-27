package com.snafu.todss.sig.exceptionhandling.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerController {
    /**
     * handled alle exceptions*/
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Map<String, String>> e(Exception e) {
        HashMap<String, String> map = new HashMap<>();
        e.printStackTrace();
        map.put("Error", e.getMessage());
        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }
}
