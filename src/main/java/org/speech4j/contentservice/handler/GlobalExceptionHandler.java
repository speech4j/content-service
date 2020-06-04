package org.speech4j.contentservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.speech4j.contentservice.dto.response.ResponseMessageDto;
import org.speech4j.contentservice.exception.EntityNotFoundException;
import org.speech4j.contentservice.exception.InternalServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ResponseMessageDto> handleEntityNotFoundException(Exception e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessageDto(e.getMessage()));
    }

    @ExceptionHandler({InternalServerException.class})
    public ResponseEntity<ResponseMessageDto> handleInternalServerException(Exception e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseMessageDto(e.getMessage()));
    }
}

