package com.microservice.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class CustomException extends RuntimeException {
    private final Error error;

    public CustomException(final Error error) {
        super(error.getMessage());
        this.error = error;
    }

    public int getCode(){
        return error.getCode();
    }
    public String getMessage() {
        return error.getMessage();
    }
    public HttpStatusCode getHttpStatusCode(){
        return error.getStatusCode();
    }
}
