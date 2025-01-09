package vn.aptech.petspa.config;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import vn.aptech.petspa.filter.JwtAuthenticationFilter;
import vn.aptech.petspa.filter.RequestLoggingFilter;
import vn.aptech.petspa.service.CustomUserDetailsService;
import vn.aptech.petspa.util.ZDebug;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable()) // Tắt CORS
                .csrf(csrf -> csrf.disable()) // Tắt CSRF
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/",
                            // "/api/**",
                            "/auth/**",
                            "/api/public/**",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/api-docs/**",
                            "/api/auth/**",
                            "/uploads/**")
                            .permitAll();
                    auth.requestMatchers("/api/admin/**").hasRole("ADMIN");
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(exception -> exception.authenticationEntryPoint(
                        (request, response, authException) -> {
                            // ZDebug.gI().ZigDebug("Unauthorized error:" + authException.getMessage());
                            // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                            sendCustomErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        }))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
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
