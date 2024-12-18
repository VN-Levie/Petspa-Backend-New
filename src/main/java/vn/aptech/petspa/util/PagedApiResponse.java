package vn.aptech.petspa.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.ResponseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
public class PagedApiResponse extends ApiResponse {
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public PagedApiResponse(String message, Object data, int currentPage, int pageSize, long totalElements, int totalPages) {
        super(STATUS_OK, message, data);
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}