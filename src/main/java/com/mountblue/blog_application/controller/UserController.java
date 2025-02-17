package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.RoleName;
import com.mountblue.blog_application.model.User;
import com.mountblue.blog_application.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //login
    //1. Provide credential = check on DB = it's correct --> "/"
    //2. Email is present but password is wrong = show message --> "incorrect credentials"
    //3. User didn't exist = take it to the register page = show message --> "email did not exist"
    @GetMapping("/login")
    public String loginPage(){
        return "login-page";
    }

    //register
    //1. Email already exist = take it to the login page = show message --> "email already exist"
    //2. Password do not match but email found in DB = show message --> "Password do not match"
    //3. New entry found = add to the DB --> "/"
    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new User());
        return "register-page";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam String confirmPassword,
                               HttpServletRequest request){ //for sessions
        //1. check if email is already present or not
        if(userService.existsByEmail(user.getEmail())){
            return "redirect:/login?error=User Already Exists";
        }

        //2. If email is present, check the provided password is also correct or not,
        // if not redirect, if yes don't do anything
        if(!user.getPassword().equals(confirmPassword)){
            return "redirect:/register?error=Password do not match";
        }

        user.setRoles(Set.of(RoleName.AUTHOR));

        System.out.println("Roles : " + user.getRoles());

        userService.save(user);
        //for sessions
        autoLogin(user, request);
        return "redirect:/";
    }

    //this method help to take new register directly to the "/" page rather than login page
    private void autoLogin(User user, HttpServletRequest request){
        //1. creates a authentication token for new users
        //don't need to check for password as they are new
        User freshUser = userService.findByEmail(user.getEmail()).orElseThrow();
        List<GrantedAuthority> authorities = freshUser.getRoles().stream()
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName.name()))// Ensure ROLE_ prefix
                .collect(Collectors.toList());

        System.out.println("User found: " + user.getEmail() + ", Role: " + user.getRoles()); // Debugging line

//        UsernamePasswordAuthenticationToken authToken =
//                new UsernamePasswordAuthenticationToken(freshUser.getEmail(), freshUser.getPassword(), authorities);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(freshUser, freshUser.getPassword(), authorities);


        //2. Stores the new token into Spring Security context
        // this will helpful as user do logging later
        SecurityContextHolder.getContext().setAuthentication(authToken);

        //3. Stores authentication in Http session, it persist request
        //so user remain login in the browser
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        System.out.println("Session ID: " + session.getId());
        System.out.println("Authentication in Context: " + SecurityContextHolder.getContext().getAuthentication());


        System.out.println("User Roles After Auto-Login: " + authorities);

    }




}
