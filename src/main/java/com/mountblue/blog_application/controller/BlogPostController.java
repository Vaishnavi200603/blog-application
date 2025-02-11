package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class BlogPostController {

    private final PostService postService;

    public BlogPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/create-post")
    public String showCreatePostForm(Model model){
        model.addAttribute("post", new Post());
        return "create-post";
    }

    @PostMapping("/newpost")
    public String createNewPost(@ModelAttribute Post post) {
        System.out.println("1. inside the createNewPost");
        postService.createAndSavePost(post);
        return "redirect:/";
    }

    @GetMapping("/")
    public String getAllPublishedPosts(Model model){
        List<Post> allPosts = postService.getAllPublishedPosts();
        model.addAttribute("posts", allPosts);
        return "all-posts";
    }

}
