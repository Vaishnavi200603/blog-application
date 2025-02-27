package com.mountblue.blog_application.service;

import com.mountblue.blog_application.model.Comment;
import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.repository.CommentRepository;
import com.mountblue.blog_application.repository.PostRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PostSecurityService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostSecurityService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public boolean isPostOwner(Long postId, String email) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            return post.getAuthorDetails().getEmail().equals(email);
        }
        return false;
    }

    public boolean isCommentOnOwnPost(Long commentId, String email) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            return comment.getPost().getAuthorDetails().getEmail().equals(email);
        }
        return false;
    }

}
