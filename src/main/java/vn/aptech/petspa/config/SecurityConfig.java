package vn.aptech.petspa.config;

import vn.aptech.petspa.filter.JwtAuthenticationFilter;
import vn.aptech.petspa.service.CustomUserDetailsService;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable()) // Bật CORS
                .csrf(csrf -> csrf.disable()) // Tắt CSRF
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/search-place/**",
                            "/get-coordinates/**",
                            "/get-address/**",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/api-docs/**",
                            "/api-docs/swagger-config",
                            "/swagger-ui.html")
                            .permitAll(); // Cho phép truy cập không cần xác thực
                    auth.requestMatchers("/api/auth/**").permitAll(); // Cho phép truy cập không cần xác thực
                    auth.requestMatchers("/api/admin/**").hasRole("ADMIN");
                    // auth.requestMatchers("/api/manager/**").hasRole("MANAGER");
                    // auth.requestMatchers("/api/staff/**").hasRole("STAFF");
                    auth.requestMatchers("/uploads/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // // Cấu hình CORS
    // @Bean
    // public CorsFilter corsFilter() {
    //     CorsConfiguration config = new CorsConfiguration();
    //     config.addAllowedOriginPattern("*"); // Cho phép tất cả origin
    //     config.addAllowedMethod("*"); // Cho phép tất cả phương thức (GET, POST, PUT, DELETE, ...)
    //     config.addAllowedHeader("*"); // Cho phép tất cả header
    //     config.setAllowCredentials(true); // Cho phép gửi cookies (nếu cần)

    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", config);

    //     return new CorsFilter(source);
    // }
}
