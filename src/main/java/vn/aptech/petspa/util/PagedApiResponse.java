package vn.aptech.petspa.util;

import lombok.AllArgsConstructor;
import lombok.Data;

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