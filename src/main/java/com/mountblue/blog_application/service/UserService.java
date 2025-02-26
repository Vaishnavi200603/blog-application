package com.mountblue.blog_application.service;

import com.mountblue.blog_application.model.RoleName;
import com.mountblue.blog_application.model.User;
import com.mountblue.blog_application.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Ensure ROLE_AUTHOR is assigned
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Collections.singleton(RoleName.AUTHOR));
        }

        System.out.println("Saving user with roles: " + user.getRoles());
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        System.out.println("userOptional : " + userOptional);
        return userOptional.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
