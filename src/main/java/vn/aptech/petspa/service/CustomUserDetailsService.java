package vn.aptech.petspa.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import vn.aptech.petspa.repository.UserRepository;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm User từ database
        Optional<vn.aptech.petspa.entity.User> optionalUser = userRepository.findByEmail(username);

        // Ném lỗi nếu không tìm thấy User
        vn.aptech.petspa.entity.User user = optionalUser
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Chuyển đổi App User thành UserDetails
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles().toArray(new String[0])) // Chuyển List thành mảng
                .build();
    }
}
