package com.dodal.meet.exception;


import com.dodal.meet.controller.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> applicationHandler(RuntimeException e) {
        log.error("ExceptionHandler - RuntimeException {}", e.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.builder()
                        .resultCode(ErrorCode.INTERNAL_SERVER_ERROR.name())
                        .result(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                        .build());
    }

    @ExceptionHandler(DodalApplicationException.class)
    public ResponseEntity<?> applicationHandler(DodalApplicationException e) {
        log.error("ExceptionHandler - DodalApplicationException  {}", e.toString());
        return ResponseEntity.status(e.getErrorCode().getStatus()).
                body(Response.builder().
                        resultCode(e.getErrorCode().name())
                        .result(e.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        log.error("ExceptionHandler - MethodArgumentNotValidException {} ", e.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.builder()
                        .resultCode(ErrorCode.INVALID_REQUEST_FIELD.name())
                        .result(bindingResult.getFieldError().getDefaultMessage())
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleValidationException(ConstraintViolationException e) {
        log.error("ExceptionHandler - ConstraintViolationException {} ", e.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.builder()
                        .resultCode(ErrorCode.INVALID_REQUEST_FIELD.name())
                        .result(parsingConstraintViolationMessage(e.getMessage()))
                        .build());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleValidationException(MissingServletRequestPartException e) {
        log.error("ExceptionHandler - MissingServletRequestPartException {} ", e.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.builder()
                        .resultCode(ErrorCode.INVALID_REQUEST_FIELD.name())
                        .result(parsingConstraintViolationMessage(e.getMessage()))
                        .build());
    }

    private String parsingConstraintViolationMessage(String message) {
        int idx = message.indexOf(":");
        return (idx < 0) ? message : message.substring(idx + 1).trim();
    }
}
