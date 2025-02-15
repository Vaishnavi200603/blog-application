package com.mountblue.blog_application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Defines config security rules,
    //where login and register pages can be accessed by anyone
    //and which other pages need authentication
    //how login and logout work
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                //1. allow access to login and register page without require password
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/", "/post/**").permitAll()
                        .requestMatchers("/create-post").hasRole("AUTHOR")
                        .anyRequest().authenticated() //require login password for other requests
                )
                //2. Custom Login logic, when it's true go to the main page if not so soemthing
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

}
