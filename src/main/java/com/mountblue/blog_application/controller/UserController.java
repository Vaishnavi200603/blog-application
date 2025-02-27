package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.RoleName;
import com.mountblue.blog_application.model.User;
import com.mountblue.blog_application.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login-page";
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new User());
        return "register-page";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam String confirmPassword,
                               HttpServletRequest request){ //for sessions
        if(userService.existsByEmail(user.getEmail())){
            return "redirect:/login?error=User Already Exists";
        }

        if(!user.getPassword().equals(confirmPassword)){
            return "redirect:/register?error=Password do not match";
        }

        user.setRoles(Set.of(RoleName.AUTHOR));
        userService.save(user);
        autoLogin(user, request);
        return "redirect:/";
    }

    private void autoLogin(User user, HttpServletRequest request){
        User freshUser = userService.findByEmail(user.getEmail()).orElseThrow();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (RoleName roleName : freshUser.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName.name()));
        }
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(freshUser, freshUser.getPassword(), authorities);

        SecurityContextHolder.getContext().setAuthentication(authToken);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }
}
