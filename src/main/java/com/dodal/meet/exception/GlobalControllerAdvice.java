package com.dodal.meet.exception;


import com.dodal.meet.DodalApplication;
import com.dodal.meet.controller.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> applicationHandler(RuntimeException e) {
        log.error("ExceptionHandler - RuntimeException {}", e.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                body(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
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
}
