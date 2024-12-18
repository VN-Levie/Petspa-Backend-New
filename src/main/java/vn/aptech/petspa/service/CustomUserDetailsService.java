package vn.aptech.petspa.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // Repository để lấy dữ liệu người dùng từ database

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Loading user by username: " + username);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        System.out.println("User found: " + user.getEmail());
        // return new org.springframework.security.core.userdetails.User(
        //         user.getEmail(),
        //         user.getPassword(),
        //         Collections.emptyList());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles().toArray(new String[0])) // Chuyển List thành mảng
                .build();
    }
}
