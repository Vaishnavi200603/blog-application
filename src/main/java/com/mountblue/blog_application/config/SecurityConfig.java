package com.mountblue.blog_application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    @Bean
    public SecurityFilterChain restApiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**")  // 🔥 Applies only to API endpoints
                .csrf(csrf -> csrf.disable()) // REST APIs are stateless, so disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/register", "/api/login").permitAll()
                        .requestMatchers("/api/create-post").hasAnyAuthority("ROLE_AUTHOR", "ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults()) // 🔥 Use Basic Auth for APIs
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No session
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/", "/post/**", "comments/add").permitAll()
                        .requestMatchers("/create-post", "/newpost").hasAnyAuthority("ROLE_AUTHOR", "ROLE_ADMIN")
                        .requestMatchers("/post/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated() //require login password for other requests
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=Invalid credentials")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
