package com.silaev.comparison.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handles exceptions withing the whole application.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> badRequest(RuntimeException rte) {
        log.debug("ControllerAdvice: badRequest");
        return ResponseEntity.badRequest().body(rte.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> noContent(RuntimeException rte) {
        log.debug("ControllerAdvice: noContent");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rte.getMessage());
    }
}
