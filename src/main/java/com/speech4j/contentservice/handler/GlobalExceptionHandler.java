package com.speech4j.contentservice.handler;

import com.speech4j.contentservice.dto.handler.ResponseMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ResponseMessageDto> handleEntityNotFoundException(Exception e) {
        LOGGER.warn(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessageDto(e.getMessage()));
    }
}
