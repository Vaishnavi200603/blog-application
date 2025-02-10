package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.Posts;
import com.mountblue.blog_application.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/post")
public class PostDetailController {
    private final PostService postService;

    public PostDetailController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public String getDetailOfPost(@PathVariable Long id, Model model){
        System.out.println("inside getDetailsOfPost");
        Posts post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "post-details";
    }
}
