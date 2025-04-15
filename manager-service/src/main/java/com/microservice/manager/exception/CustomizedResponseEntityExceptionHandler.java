package com.microservice.manager.exception;

import com.microservice.manager.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ApiResponse<String>> handleBadRequestException(BadRequestException ex) {
        ApiResponse<String> response = new ApiResponse<>(
                false,
                ex.getMessage(),
                ""
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<ApiResponse<String>> handleCustomException(CustomException customException){
        ApiResponse<String> response = new ApiResponse<>(
                false,
                customException.getMessage(),
                ""
        );
        return new ResponseEntity<>(response, customException.getHttpStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiResponse<String>> handleAllException(Exception e, WebRequest request) {
        ApiResponse<String> response = new ApiResponse<>(
                false,
                e.getMessage(),
                ""
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
