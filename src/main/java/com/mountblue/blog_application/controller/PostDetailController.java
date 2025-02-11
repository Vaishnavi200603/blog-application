package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        System.out.println("2. inside getDetailsOfPost");
        Optional<Post> post = postService.getPostById(id);
        if(post.isPresent()){
            model.addAttribute("post", post.get());
            return "post-details";
        }
        else{
            return "error-page";
        }
    }

    @GetMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, Model model){
        Optional<Post> post = postService.getPostById(id);

        post.get().setTagNames(postService.setTagsNameFromTags(id));
//        System.out.println("In Edit id : " + post.get().getTagNames());
        model.addAttribute("post", post.get());
        return "update-post";
    }

    @PostMapping("/update/{id}")
    public String showUpdatedPost(@PathVariable Long id, @ModelAttribute Post updatedPost){
//        System.out.println("UPDATED POST : " + updatedPost.getTitle());
//        System.out.println("UPDATED POST : " + updatedPost.getTagNames());
        postService.updatePost(id, updatedPost);
        return "redirect:/post/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return "redirect:/";
    }
}
