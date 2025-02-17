package com.mountblue.blog_application.config;

import com.mountblue.blog_application.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    //Defines config security rules,
    //where login and register pages can be accessed by anyone
    //and which other pages need authentication
    //how login and logout work
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
//                .csrf(csrf -> csrf.disable()) // Disable CSRF
                //1. allow access to login and register page without require password
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/", "/post/**", "comments/add").permitAll()
                        .requestMatchers("/create-post", "/newpost").hasAnyAuthority("ROLE_AUTHOR", "ROLE_ADMIN")
                        .requestMatchers("/post/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated() //require login password for other requests
                )
                //2. Custom Login logic, when it's true go to the main page if not so something
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=Invalid credentials")
                        .permitAll()
                )
                //3. logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .build(); //built the security filter chain that built rules
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        PasswordEncoder encoder = passwordEncoder();
//
//        // Creating a static ADMIN user with hardcoded credentials
//        UserDetails adminUser = User.builder()
//                .username("admin@gmail.com")
//                .password(encoder.encode("admin123")) // Default password
//                .authorities("ROLE_ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(adminUser);
//    }

    //handle authentication manually
    //It checks the provided username and password against the configured UserDetailsService and PasswordEncoder.
    // ✅ Modify the existing AuthenticationManager to use CustomUserDetailService
    // ✅ Updated AuthenticationManager method using AuthenticationConfiguration
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
