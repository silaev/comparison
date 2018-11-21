package com.silaev.comparison.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.xml.bind.ValidationException;

/**
 * Handles exceptions withing the whole application.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InvalidFormatException.class, ValidationException.class})
    public ResponseEntity<String> nonProcessableEntityException(RuntimeException rte) {
        log.debug("nonProcessableEntityException");
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(rte.getMessage());
    }

    @ExceptionHandler({JsonParseException.class})
    public ResponseEntity<String> invalidJsonEntityException(RuntimeException rte) {
        log.debug("invalidJsonEntityException");
        return ResponseEntity.badRequest().body(rte.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> illegalStateException(RuntimeException rte) {
        log.debug("illegalStateException");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rte.getMessage());
    }
}
