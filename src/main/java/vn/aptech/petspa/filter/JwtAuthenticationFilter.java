package vn.aptech.petspa.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.aptech.petspa.repository.UserRepository;
import vn.aptech.petspa.service.CustomUserDetailsService;
import vn.aptech.petspa.util.JwtUtil;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    public String HEADER_STRING = "Authorization";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String username = null;
        String authToken = null;

        authToken = getJwtFromRequest(req);

        try {
            username = jwtUtil.extractUsername(authToken);
        } catch (Exception e) {
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // // Lưu vào request
                // User user = userRepository.findByEmail(email)
                // .orElseThrow(() -> new IllegalArgumentException("User not found"));
                // request.setAttribute("user", user);
            }
        }

        filterChain.doFilter(req, response);

    }

    // Hàm lấy JWT từ header Authorization
    private String getJwtFromRequest(HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader("Authorization");
            return (bearerToken != null && bearerToken.startsWith("Bearer "))
                    ? bearerToken.substring(7)
                    : null;
        } catch (Exception e) {
            return null;
        }
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

}
