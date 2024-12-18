package vn.aptech.petspa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Hidden;
import vn.aptech.petspa.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Xử lý IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("IllegalArgumentException: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ApiResponse(ApiResponse.STATUS_BAD_REQUEST));
    }

    // Xử lý JsonProcessingException
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiResponse> handleJsonProcessingException(JsonProcessingException e) {
        logger.warn("JsonProcessingException: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ApiResponse(ApiResponse.STATUS_BAD_REQUEST, "Invalid JSON format"));
    }

    // Xử lý RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {
        logger.error("RuntimeException: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(ApiResponse.STATUS_INTERNAL_SERVER_ERROR));
    }

    // Xử lý JwtException
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse> handleJwtException(JwtException e) {
        logger.warn("JwtException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(ApiResponse.STATUS_UNAUTHORIZED, "Unauthorized"));
    }

    // Xử lý ngoại lệ chung
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception e) {
        logger.error("Unhandled exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(ApiResponse.STATUS_INTERNAL_SERVER_ERROR, "Something went wrong"));
    }
}
