package vn.aptech.petspa.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.ResponseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
public class ApiResponse {
    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    private int status;
    private String message;
    private Object data;

    public ApiResponse() {
        // Constructor mặc định trống
    }

    public ApiResponse(int status, String message) {
        this(status, message, null);
    }

    public ApiResponse(Object data) {
        this(STATUS_OK, "Success", data);
    }

    public static ResponseEntity<ApiResponse> badRequest(String message) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse(STATUS_BAD_REQUEST, message));
    }

    public static ResponseEntity<ApiResponse> unauthorized(String message) {
        return ResponseEntity.status(STATUS_UNAUTHORIZED)
                .body(new ApiResponse(STATUS_UNAUTHORIZED, message));
    }

    public static ResponseEntity<ApiResponse> internalServerError(String message) {
        return ResponseEntity.status(STATUS_INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(STATUS_INTERNAL_SERVER_ERROR, message));
    }
}
