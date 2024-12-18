package vn.aptech.petspa.util;

import java.util.List;

import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import vn.aptech.petspa.dto.VerifyDTO;
import vn.aptech.petspa.entity.Pet;

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
    public static final int STATUS_REQUEST_TIMEOUT = 408;

    private int status;
    private String message;
    private Object data;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    // constructor
    public ApiResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiResponse(Object data) {
        this.status = STATUS_OK;
        this.message = "Success";
    }

    public static ResponseEntity<ApiResponse> responsebadRequest(String msg) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse(ApiResponse.STATUS_BAD_REQUEST, msg));
    }

    public ApiResponse(int statusOk, String string, Object data) {
        this.status = statusOk;
        this.message = string;
        this.data = data;
    }

    public ApiResponse(String string, List<Pet> content, int number, int size, long totalElements, int totalPages) {
        this.message = string;
        this.data = content;
        this.currentPage = number;
        this.pageSize = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

}
