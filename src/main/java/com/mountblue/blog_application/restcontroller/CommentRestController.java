package com.mountblue.blog_application.restcontroller;

import com.mountblue.blog_application.dtos.CommentDTO;
import com.mountblue.blog_application.dtos.UpdateCommentDTO;
import com.mountblue.blog_application.model.Comment;
import com.mountblue.blog_application.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    private final CommentService commentService;

    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody CommentDTO request) {
        try {
            commentService.saveComment(
                    request.getPostId(),
                    request.getName(),
                    request.getEmail(),
                    request.getCommentText(),
                    request.getParentCommentId()
            );
            return ResponseEntity.ok("Comment added successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Unable to add comment.");
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<String> updateComment(
            @PathVariable Long id,
            @RequestBody UpdateCommentDTO request) {

        commentService.updateComment(id, request.getUpdatedComment());
        return ResponseEntity.ok("Comment updated successfully!");
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable("id") Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok("Comment deleted successfully!");
    }
}