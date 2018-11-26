package com.silaev.comparison.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

/**
 * Handles exceptions withing the whole application.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> noContent(RuntimeException rte) {
        log.debug("ControllerAdvice: noContent. {}", rte.getMessage());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rte.getMessage());
    }

    @ExceptionHandler({DuplicateKeyException.class, ResponseStatusException.class})
    public ResponseEntity<String> badRequest(RuntimeException rte) {
        log.debug("ControllerAdvice: badRequest, {}", rte.getMessage());
        return ResponseEntity.badRequest().body(rte.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        log.debug("ControllerAdvice: handleGeneralException. {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }
}
