package com.mountblue.blog_application.service;

import com.mountblue.blog_application.model.RoleName;
import com.mountblue.blog_application.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email)
        );

        System.out.println("USER : " + user);

        Set<GrantedAuthority> authorities = new HashSet<>();
        for (RoleName role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }

        System.out.println("ROLES : " + user.getRoles());
        System.out.println("User : " + user);

        return user;
    }
}
