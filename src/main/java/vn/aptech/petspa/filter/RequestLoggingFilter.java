package vn.aptech.petspa.filter;

import org.springframework.stereotype.Component;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

@Component
public class RequestLoggingFilter implements Filter {

    // Định nghĩa màu sắc ANSI cho console
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\u001B[36m";

    @Override
    public void init(FilterConfig filterConfig) {
        // Khởi tạo nếu cần
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        // Lấy URL đầy đủ
        String fullUrl = req.getRequestURL().toString();
        String queryString = req.getQueryString();
        if (queryString != null) {
            fullUrl += "?" + queryString;
        }

        // Lấy thời gian hiện tại và định dạng
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");
        String time = LocalDateTime.now().format(formatter);

        // Lấy phương thức (GET, POST...)
        String method = req.getMethod();

        // In thông tin request với màu sắc
        System.out.println(ANSI_GREEN + "[" + time + "]" + ANSI_RESET + " " 
                + ANSI_YELLOW + method + ANSI_RESET + " "
                + ANSI_CYAN + fullUrl + ANSI_RESET);

        // In tất cả các header của request
        Enumeration<String> headerNames = req.getHeaderNames();
        // while (headerNames.hasMoreElements()) {
        //     String headerName = headerNames.nextElement();
        //     System.out.println(ANSI_CYAN + "  Header: " + ANSI_RESET + headerName 
        //             + " = " + req.getHeader(headerName));
        // }

        // Tiếp tục cho phép request đi tiếp
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup nếu cần khi filter bị hủy
    }
}
