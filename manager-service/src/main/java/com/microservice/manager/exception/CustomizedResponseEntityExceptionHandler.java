package com.microservice.manager.exception;

import com.microservice.manager.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ApiResponse<String>> handleBadRequestException(BadRequestException ex, WebRequest request) {
        ApiResponse<String> response = new ApiResponse<>(
                false,
                ex.getMessage(),
                ""
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntime(RuntimeException ex) {
        if ("EMAIL_EXISTS".equals(ex.getMessage())) {
            ApiResponse<Object> response = new ApiResponse<>(false, "Email already exists", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Server error", null));
    }

    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<ApiResponse<String>> handleCustomException(CustomException cx, WebRequest request) {
        ApiResponse<String> response = new ApiResponse<>(
                false,
                cx.getMessage(),
                ""
        );
        return new ResponseEntity<>(response, cx.getError().getStatusCode());
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
