package vn.aptech.petspa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Hidden;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.ZDebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Xử lý IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        // logger.warn("IllegalArgumentException: {}", e.getMessage());
        ZDebug.gI().error("IllegalArgumentException.throw: " + e.getMessage());
        return ApiResponse.badRequest(e.getMessage());
    }

    // Xử lý JsonProcessingException
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiResponse> handleJsonProcessingException(JsonProcessingException e) {
        // logger.warn("JsonProcessingException: {}", e.getMessage());
        ZDebug.gI().error("JsonProcessingException.throw: " + e.getMessage());
        return ApiResponse.badRequest("Invalid request body");
    }

    // Xử lý RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {
        // logger.error("RuntimeException: {}", e.getMessage(), e);
        ZDebug.gI().error("RuntimeException.throw: " + e.getMessage());
        return ApiResponse.internalServerError(e.getMessage());
        // return ApiResponse.internalServerError("Something went wrong");
    }

    // Xử lý JwtException
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse> handleJwtException(JwtException e) {
        // logger.warn("JwtException: {}", e.getMessage());
        ZDebug.gI().error("JwtException.throw: " + e.getMessage());
        return ApiResponse.unauthorized("Invalid token");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse> handleExpiredJwtException(ExpiredJwtException e) {
        // logger.warn("JwtException: {}", e.getMessage());
        ZDebug.gI().error("ExpiredJwtException.throw: " + e.getMessage());
        return ApiResponse.unauthorized("Invalid token");
    }

    // Xử lý ngoại lệ chung
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception e) {
        // logger.error("Unhandled exception: {}", e.getMessage(), e);
        ZDebug.gI().error("Exception.throw: " + e.getMessage());
        // return ApiResponse.internalServerError("Something went wrong");
        return ApiResponse.internalServerError(e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(NoResourceFoundException ex) {
        return ApiResponse.notFound(ex.getMessage());
    }
}
