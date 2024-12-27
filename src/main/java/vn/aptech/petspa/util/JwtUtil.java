package vn.aptech.petspa.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    // Key bảo mật
    private final String SECRET_KEY = "50ae2cfcf2c0a1eec114dc8b9283a27bf7b96b9379145997e666ad74f95ae67f5521f95472f52b380f0a1df3564af7f8a3809243c4103f4a87bd7653de5e02db11b1f2fbb667dfd9322e445c392ea3d96282ab3022c942ea41c4af29be8d2a1ca81120122a73f7e159561be5d7811cb452ae9016bae7237f7438714ae265be2e75578a373d29c6c7b7c0523c67bdbd87bc1ff1165cc003f45b9418db98be4ecf1e7f1c730e00a76578c6eb5227bd6e5234ed2449aae249040d4ec9580ecd33dd6ccb30d60e5509ba03a5052220c1bcd3c356447608da71865dbd358838f3996086d1b0b28f3917820f7c9899927258587ad892bfba3dc9ca18ee16e701404ad1";

    private final SecretKey secret = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Thời gian hết hạn
    private final long jwtExpirationInMs = 3600000; // 1 hour
    private final long refreshTokenExpirationInMs = jwtExpirationInMs * 24 * 90; // 90 days
    private final long otpExpirationInMs = 600000; // 10 minutes

    // constance
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String OTP_TOKEN = "otp_token";
    public static final String VERIFICATION_TOKEN = "verification_token";

    /**
     * Generate token theo type: access_token, refresh_token, otp
     */
    public String generateToken(UserDetails userDetails, String type) {
        Date now = new Date();
        Date expiryDate;

        // Tính toán thời gian hết hạn
        expiryDate = switch (type) {
            case ACCESS_TOKEN -> new Date(now.getTime() + jwtExpirationInMs);
            case REFRESH_TOKEN -> new Date(now.getTime() + refreshTokenExpirationInMs);
            case OTP_TOKEN -> new Date(now.getTime() + otpExpirationInMs);
            default -> throw new IllegalArgumentException("Invalid token type: " + type);
        };

        // In thời gian hết hạn dưới dạng dd/MM/yyyy HH:mm:ss
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        System.out.println("Thời gian hết hạn: " + dateFormat.format(expiryDate) + " (" + type + ")");

        // Trả về token
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .claim("type", type)
                .setIssuedAt(now)
                .setExpiration(expiryDate) // Sử dụng biến expiryDate đã tính toán
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Giải mã và lấy username từ token
     */
    public String extractEmail(String token) {
        try {
            token = extractToken(token);
            if (token == null) {
                return null;
            }
            return Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token, String requiredType) {
        try {
            var claims = Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
            String tokenType = claims.get("type", String.class);
            System.out.println("Token type: " + tokenType);
            if (!requiredType.equals(tokenType)) {
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            throw ex;
        }

    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

    public String generateVerificationToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + otpExpirationInMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .claim("type", VERIFICATION_TOKEN)
                .setExpiration(expiryDate)
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }

    // hàm trích xuất token ( token = token.replace("Bearer ", "");)
    public String extractToken(String token) {
        if (token == null) {
            return null; // hoặc trả về giá trị mặc định
        }
        return token.replace("Bearer ", "");
    }

    // UNAUTHORIZED
    public ResponseEntity<ApiResponse> responseUnauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(ApiResponse.STATUS_UNAUTHORIZED, "Unauthorized"));
    }

    public ResponseEntity<ApiResponse> responseInternalServerError(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(ApiResponse.STATUS_INTERNAL_SERVER_ERROR, "Something went wrong"));
    }

}
