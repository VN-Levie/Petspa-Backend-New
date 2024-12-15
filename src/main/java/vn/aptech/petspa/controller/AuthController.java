package vn.aptech.petspa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import vn.aptech.petspa.dto.LoginDTO;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            String email = loginDTO.getEmail();
            String password = loginDTO.getPassword();

            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                ApiResponse response = new ApiResponse(ApiResponse.STATUS_BAD_REQUEST,
                        "Email or password is empty!", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Authenticate
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            // Load user details and generate JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String jwt_token = jwtUtil.generateToken(userDetails, JwtUtil.ACCESS_TOKEN);

            ApiResponse response = new ApiResponse(ApiResponse.STATUS_OK, "Login successfully!", jwt_token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse response = new ApiResponse(ApiResponse.STATUS_UNAUTHORIZED,
                    "Invalid email or password!", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

}