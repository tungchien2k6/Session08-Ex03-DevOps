package com.ra.appointmentservice.exception;

import com.ra.appointmentservice.dto.response.ApiResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponseError> handleResponseStatusException(ResponseStatusException ex) {
        ApiResponseError error = ApiResponseError.builder()
                .message(ex.getReason())
                .status(ex.getStatusCode().value())
                .error("Doctor Service Error")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, ex.getStatusCode());
    }
}