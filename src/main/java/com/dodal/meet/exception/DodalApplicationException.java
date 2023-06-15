package com.dodal.meet.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DodalApplicationException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;
    public DodalApplicationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
}
