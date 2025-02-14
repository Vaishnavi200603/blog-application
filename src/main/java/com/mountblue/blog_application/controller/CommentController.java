package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.Comment;
import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.service.CommentService;
import com.mountblue.blog_application.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private final PostService postService;
    private final CommentService commentService;

    public CommentController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    //for add
    @PostMapping("/add")
    public String addComment(
            @RequestParam("postId") Long postId,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("comment") String commentText,
            @RequestParam(required = false) Long parentCommentId) {

        commentService.saveComment(postId, name, email, commentText, parentCommentId);
        return "redirect:/post/" + postId;
    }

    //for update
    @PostMapping("/update/{id}")
    public String updateComment(@PathVariable("id") Long id,
                                @RequestParam("postId") Long postId,
                                @RequestParam("updatedComment") String updatedComment){
        System.out.println("UPDATED Comment : " + updatedComment);
        commentService.updateComment(id, updatedComment);
        return "redirect:/post/" + postId;
    }

    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable("id") Long id,
                                @RequestParam("postId") Long postId){
        commentService.deleteComment(id);
        return "redirect:/post/" + postId;
    }


}
