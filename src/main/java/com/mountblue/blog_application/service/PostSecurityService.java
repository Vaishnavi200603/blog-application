package com.mountblue.blog_application.service;

import com.mountblue.blog_application.repository.CommentRepository;
import com.mountblue.blog_application.repository.PostRepository;
import org.springframework.stereotype.Component;

@Component
public class PostSecurityService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostSecurityService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public boolean isPostOwner(Long postId, String email) {
        return postRepository.findById(postId)
                .map(post -> post.getAuthorDetails().getEmail().equals(email))
                .orElse(false); // Return false if post is not found
    }

    public boolean isCommentOnOwnPost(Long commentId, String email) {
        return commentRepository.findById(commentId)
                .map(comment -> comment.getPost().getAuthorDetails().getEmail().equals(email))
                .orElse(false);
    }
}
