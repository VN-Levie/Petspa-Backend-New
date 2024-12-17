package vn.aptech.petspa.filter;

import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // Khởi tạo nếu cần
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        // Log thông tin request
        System.out.println("Request Method: " + req.getMethod());
        System.out.println("Request URI: " + req.getRequestURI());
        System.out.println("Request Query String: " + req.getQueryString());
        System.out.println("Request Headers: " + req.getHeaderNames());

        // Tiếp tục gửi request đến các Filter tiếp theo hoặc Controller
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup khi kết thúc
    }
}
