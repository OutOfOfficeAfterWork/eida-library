package com.example.springapp.config;

import com.example.springapp.config.auth.IllegalAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Void> on(IllegalAuthException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
