package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.Posts;
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
        model.addAttribute("post", new Posts());
        return "create-post";
    }

    @PostMapping("/newpost")
    public String createNewPost(@ModelAttribute Posts post) {
        System.out.println("inside the createNewPost");

        try {
            postService.createAndSavePost(post);
            return "all-posts";
        } catch (Exception e) {
            System.err.println("Error while creating new post: " + e.getMessage());
            return "error-page";
        }
    }

    @GetMapping("/")
    public String getAllPublishedPosts(Model model){
        List<Posts> allPosts = postService.getAllPublishedPosts();
        model.addAttribute("posts", allPosts);
        return "all-posts";
    }

}
