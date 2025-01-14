package vn.aptech.petspa.config;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import vn.aptech.petspa.filter.JwtAuthenticationFilter;
import vn.aptech.petspa.filter.RequestLoggingFilter;
import vn.aptech.petspa.util.ZDebug;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS và CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Bật CORS
                .csrf(csrf -> csrf.disable()) // Tắt CSRF cho API REST

                // Phân quyền endpoint
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/auth/**", // Endpoint auth
                            "/api/auth/**", // Endpoint auth
                            "/api/payment/**", // Endpoint auth
                            "/api/map/**", // Endpoint auth
                            "/api/public/**", // Public API
                            "/swagger-ui/**", // Swagger
                            "/api-docs/**", // Swagger
                            "/v3/api-docs/**", // Swagger docs
                            "/uploads/**" // Upload files
                    ).permitAll(); // Không yêu cầu xác thực
                    auth.requestMatchers("/api/admin/**").hasRole("ADMIN"); // Yêu cầu role ADMIN
                    auth.requestMatchers("/api/**").authenticated(); // Cần xác thực với các API còn lại
                    auth.anyRequest().denyAll(); // Từ chối các request không được định nghĩa
                })

                // Cấu hình session là stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Xử lý lỗi xác thực
                .exceptionHandling(exception -> exception.authenticationEntryPoint(
                        (request, response, authException) -> {
                            // authException.printStackTrace();
                            // ZDebug.gI().ZigDebug("Unauthorized error:" + authException.getMessage());
                            // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                            // Full authentication is required to access this resource
                            String accessDeniedMessage = "You need to login to access this resource";
                            sendCustomErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, accessDeniedMessage);
                        }))

                // Thêm JWT Filter trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // Origin của frontend
        configuration.addAllowedMethod("*"); // Tất cả method: GET, POST, PUT, DELETE, OPTIONS
        configuration.addAllowedHeader("*"); // Tất cả headers
        configuration.setAllowCredentials(true); // Hỗ trợ gửi cookie/xác thực
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng CORS cho tất cả endpoint
        return source;
    }

    private void sendCustomErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status);
        errorDetails.put("message", message);
        errorDetails.put("timestamp", System.currentTimeMillis());

        ObjectMapper objectMapper = new ObjectMapper();
        OutputStream out = response.getOutputStream();
        objectMapper.writeValue(out, errorDetails);
        out.flush();
    }

    // Đăng ký RequestLoggingFilter với order thấp hơn
    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> loggingFilterRegistration(RequestLoggingFilter filter) {
        FilterRegistrationBean<RequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(Integer.MIN_VALUE); // Đảm bảo chạy trước tất cả
        return registrationBean;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
