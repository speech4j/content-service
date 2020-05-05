package org.speech4j.contentservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.speech4j.contentservice.dto.handler.ResponseMessageDto;
import org.speech4j.contentservice.exception.EntityNotFoundException;
import org.speech4j.contentservice.exception.InternalServerException;
import org.speech4j.contentservice.exception.TenantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler({InternalServerException.class})
    public ResponseEntity<ResponseMessageDto> handleInternalServerException(Exception e) {
        LOGGER.warn(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseMessageDto(e.getMessage()));
    }

    @ExceptionHandler({TenantNotFoundException.class})
    public ResponseEntity<ResponseMessageDto> handleTenantNotFoundException(Exception e) {
        LOGGER.warn(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessageDto(e.getMessage()));
    }
}

