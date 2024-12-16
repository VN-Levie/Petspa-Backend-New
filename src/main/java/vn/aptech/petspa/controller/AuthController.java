package vn.aptech.petspa.controller;

import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.benmanes.caffeine.cache.Cache;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import vn.aptech.petspa.dto.LoginDTO;
import vn.aptech.petspa.dto.RegisterDTO;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.repository.UserRepository;
import vn.aptech.petspa.service.EmailService;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository; // Đảm bảo repository được inject

    @Autowired
    private Cache<String, String> otpCache;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Cache<String, Integer> otpAttemptCache; // Inject cache đếm số lần gửi lại OTP

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            User user = userRepository.findByEmail(loginDTO.getEmail()).orElse(null);
            if (user == null || !user.isVerified()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(ApiResponse.STATUS_UNAUTHORIZED, "Email is not verified!", null));
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());
            String jwtToken = jwtUtil.generateToken(userDetails, JwtUtil.ACCESS_TOKEN);

            ApiResponse response = new ApiResponse(ApiResponse.STATUS_OK, "Login successfully!", jwtToken);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            logger.error("Authentication failed: Invalid email or password", e);
            ApiResponse response = new ApiResponse(ApiResponse.STATUS_UNAUTHORIZED, "Invalid email or password!", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            logger.error("An error occurred during login process", e);
            ApiResponse response = new ApiResponse(ApiResponse.STATUS_INTERNAL_SERVER_ERROR,
                    "An error occurred during login process", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String token) {
        // 1. (Tuỳ chọn) Blacklist token tại server nếu cần
        // jwtUtil.invalidateToken(token);

        ApiResponse response = new ApiResponse(ApiResponse.STATUS_OK, "Logout successfully!", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            // Giải mã và kiểm tra refresh token
            String username = jwtUtil.extractEmail(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                // Tạo access token mới
                String newAccessToken = jwtUtil.generateToken(userDetails, JwtUtil.ACCESS_TOKEN);
                ApiResponse response = new ApiResponse(ApiResponse.STATUS_OK, "Token refreshed successfully!",
                        newAccessToken);
                return ResponseEntity.ok(response);
            } else {
                throw new IllegalArgumentException("Invalid refresh token");
            }
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(ApiResponse.STATUS_UNAUTHORIZED, "Invalid or expired refresh token!",
                    null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
        // Kiểm tra xem email có tồn tại trong hệ thống không
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (userDetails == null) {
            ApiResponse response = new ApiResponse(ApiResponse.STATUS_BAD_REQUEST, "Email not found!", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Tạo reset token
        String resetToken = jwtUtil.generateToken(userDetails, JwtUtil.OTP_TOKEN);

        // Gửi email chứa token (giả lập)
        logger.info("Reset token for {}: {}", email, resetToken);

        ApiResponse response = new ApiResponse(ApiResponse.STATUS_OK, "Forgot password email sent successfully!", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            // Giải mã token
            String username = jwtUtil.extractEmail(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                // Cập nhật mật khẩu (giả lập)
                logger.info("Password updated for user: {}", username);

                ApiResponse response = new ApiResponse(ApiResponse.STATUS_OK, "Password reset successfully!", null);
                return ResponseEntity.ok(response);
            } else {
                throw new IllegalArgumentException("Invalid or expired reset token");
            }
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(ApiResponse.STATUS_BAD_REQUEST,
                    "Invalid token or unable to reset password", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterDTO registerDTO) {
        try {
            String email = registerDTO.getEmail();

            // Kiểm tra xem email đã tồn tại chưa thông qua UserRepository
            boolean userExists = userRepository.findByEmail(email).isPresent();
            if (userExists) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(ApiResponse.STATUS_BAD_REQUEST, "Email is already registered!", null));
            }

            // Generate OTP (6-digit numeric)
            Random random = new Random();
            String otp = String.format("%06d", random.nextInt(1000000)); // Tạo số từ 0 đến 999999
            String verificationToken = jwtUtil.generateVerificationToken(email);

            // Lưu OTP vào cache
            otpCache.put(email, otp);

            // Gửi email
            String verificationLink = "http://localhost:8090/auth/verify?token=" + verificationToken;
            emailService.sendOtpMail(email, "Account Verification", otp, verificationLink);

            // Mã hóa mật khẩu và lưu user mới
            String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
            User newUser = new User(registerDTO.getName(), email, encodedPassword, Set.of("USER"), false);
            userRepository.save(newUser);

            return ResponseEntity.ok(
                    new ApiResponse(ApiResponse.STATUS_OK, "Registration successful. Please check your email.", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(ApiResponse.STATUS_INTERNAL_SERVER_ERROR,
                            "An error occurred during registration.", null));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOtp(@RequestParam String email) {
        // Kiểm tra email đã tồn tại chưa
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(ApiResponse.STATUS_BAD_REQUEST, "Email is not registered!", null));
        }

        if (user.isVerified()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(ApiResponse.STATUS_BAD_REQUEST, "Email is already verified!", null));
        }

        // Kiểm tra số lần gửi OTP trong 10 phút
        Integer attempts = otpAttemptCache.getIfPresent(email);
        if (attempts != null && attempts >= 3) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ApiResponse(ApiResponse.STATUS_BAD_REQUEST,
                            "You have exceeded the maximum resend attempts. Please try again later.", null));
        }

        // Tăng số lần gửi OTP hoặc khởi tạo
        otpAttemptCache.put(email, (attempts == null ? 1 : attempts + 1));

        // Tạo OTP
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));
        String verificationToken = jwtUtil.generateVerificationToken(email);

        otpCache.put(email, otp); // Lưu OTP vào cache

        // Gửi email
        String verificationLink = "http://localhost:8090/auth/verify?token=" + verificationToken;
        emailService.sendOtpMail(email, "Account Verification", otp, verificationLink);

        return ResponseEntity.ok(
                new ApiResponse(ApiResponse.STATUS_OK, "OTP sent successfully. Please check your email.", null));
    }

    @GetMapping("/verify")
    public ResponseEntity<Resource> verify(@RequestParam String token) {
        try {
            if (jwtUtil.validateToken(token, JwtUtil.VERIFICATION_TOKEN)) {
                String email = jwtUtil.extractEmail(token);

                // Tìm user trong database
                User user = userRepository.findByEmail(email).orElse(null);
                if (user == null) {
                    return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/html")
                            .body(new ClassPathResource("static/verification_error_user.html"));
                }
                // Cập nhật trạng thái đã xác thực
                if (user.isVerified()) {
                    return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/html")
                            .body(new ClassPathResource("static/verification_error.html"));
                }

                // Cập nhật trạng thái đã xác thực
                if (!user.isVerified()) {
                    user.setVerified(true);
                    userRepository.save(user);
                }

                // Trả về file HTML tĩnh
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/html")
                        .body(new ClassPathResource("static/verification_success.html"));
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/html")
                    .body(new ClassPathResource("static/verification_error.html"));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/html")
                    .body(new ClassPathResource("static/verification_error.html"));
        } catch (Exception e) {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/html")
                    .body(new ClassPathResource("static/verification_error.html"));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        String cachedOtp = otpCache.getIfPresent(email);
        if (cachedOtp != null && cachedOtp.equals(otp)) {
            // Tìm user trong database
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(ApiResponse.STATUS_BAD_REQUEST, "User not found.", null));
            }

            // Kiểm tra trạng thái xác thực
            if (user.isVerified()) {
                return ResponseEntity.ok(new ApiResponse(ApiResponse.STATUS_OK, "Email already verified.", null));
            }

            // Cập nhật trạng thái đã xác thực
            user.setVerified(true);
            userRepository.save(user);

            // Xóa OTP khỏi cache
            otpCache.invalidate(email);

            return ResponseEntity.ok(new ApiResponse(ApiResponse.STATUS_OK, "OTP verified successfully!", null));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(ApiResponse.STATUS_BAD_REQUEST, "Invalid OTP.", null));
    }

}