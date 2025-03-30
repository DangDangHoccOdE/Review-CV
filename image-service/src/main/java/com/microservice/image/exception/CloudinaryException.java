package com.microservice.image.exception;

public class CloudinaryException extends RuntimeException{
    private final Error error;

    public CloudinaryException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
