package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.Comment;
import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.service.CommentService;
import com.mountblue.blog_application.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/post")
public class PostDetailController {
    private final PostService postService;
    private final CommentService commentService;

    public PostDetailController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public String getDetailOfPost(@PathVariable Long id, Model model){
        System.out.println("2. inside getDetailsOfPost");
        Optional<Post> post = postService.getPostById(id);
        if(post.isPresent()){
            List<Comment> comments = commentService.getCommentsByPost(id);

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
