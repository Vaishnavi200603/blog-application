package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.Comment;
import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.model.User;
import com.mountblue.blog_application.service.CommentService;
import com.mountblue.blog_application.service.PostService;
import com.mountblue.blog_application.service.UserService;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/post")
public class PostDetailController {
    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    public PostDetailController(PostService postService, CommentService commentService, UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String getDetailOfPost(@PathVariable Long id, Model model, Principal principal){
        System.out.println("2. inside getDetailsOfPost");
        Optional<Post> post = postService.getPostById(id);
        if(post.isPresent()){
            System.out.println("✅ Post found: " + post.get().getTitle() + " | Author: " + post.get().getAuthor());

            List<Comment> comments = commentService.getCommentsByPost(id);

            Comment newComment = new Comment();
            if (principal != null) { // If user is logged in
                User loggedInUser = userService.getUserByEmail(principal.getName());
                newComment.setName(loggedInUser.getName());
                newComment.setEmail(loggedInUser.getEmail());
            }

            model.addAttribute("post", post.get());
            model.addAttribute("comments", comments); //for existing comments
            model.addAttribute("newComment", new Comment());  //for adding new comments
            return "post-details";
        }
        else{
            return "error-page";
        }
    }

    @GetMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, Model model, Principal principal){
        Optional<Post> post = postService.getPostById(id);
        if (post.isPresent()) {
            post.get().setTagNames(postService.setTagsNameFromTags(id));
            model.addAttribute("post", post.get());
            return "update-post";
        } else {
            return "redirect:/error";
        }
    }


    @PostMapping("/update/{id}")
    public String showUpdatedPost(@PathVariable Long id, @ModelAttribute Post updatedPost){
        postService.updatePost(id, updatedPost);
        return "redirect:/post/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return "redirect:/";
    }


}
