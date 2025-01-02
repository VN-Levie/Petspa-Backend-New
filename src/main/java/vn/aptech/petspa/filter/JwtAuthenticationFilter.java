package vn.aptech.petspa.filter;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.repository.UserRepository;
import vn.aptech.petspa.service.CustomUserDetailsService;
import vn.aptech.petspa.util.JwtUtil;
import vn.aptech.petspa.util.ZDebug;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String jwt = getJwtFromRequest(request);
        if (jwt == null) {
            // System.out.println("JWT is null");
            // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            // SecurityContextHolder.clearContext();
            ZDebug.gI().ZigDebug("JWT is null");
            filterChain.doFilter(request, response);
            return;
        }
        String username = null;
        if (jwt != null) {
            ZDebug.gI().ZigDebug("!JWT is null");

            username = jwtUtil.extractEmail(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    // Lưu vào request
                    User user = userRepository.findByEmail(username)
                            .orElseThrow(() -> new IllegalArgumentException("User not found"));
                    request.setAttribute("user", user);
                } else {
                    logger.warn("JWT validation failed for user: " + username);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                }
            }
        }

        filterChain.doFilter(request, response);
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
}
