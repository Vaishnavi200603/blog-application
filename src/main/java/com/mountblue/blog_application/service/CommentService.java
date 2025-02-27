package com.mountblue.blog_application.service;

import com.mountblue.blog_application.model.Comment;
import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.repository.CommentRepository;
import com.mountblue.blog_application.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    //for post detail controller
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdAndParentCommentIsNull(postId);
    }

    //saving - "/comments/add
    @Transactional
    public void saveComment(Long postId, String name, String email, String commentText, Long parentCommentId) {
        Optional<Post> postOptional = postRepository.findById(postId);

        if(postOptional.isPresent()){
            Comment comment = new Comment();
            comment.setPost(postOptional.get());
            comment.setName(name);
            comment.setEmail(email);
            comment.setComment(commentText);

            //if it is a reply to any comment
            if(parentCommentId != null){
                comment.setParentComment(commentRepository.findById(
                        parentCommentId).orElse(null));
            }

            commentRepository.save(comment);
        }
    }

    //update - "/comments/update/{id}
    @Transactional
    public void updateComment(Long commentId, String updatedComment){
        Optional<Comment> existingComment = commentRepository.findById(commentId);
        if(existingComment.isPresent()){
            Comment comment = existingComment.get();
            comment.setComment(updatedComment);
            commentRepository.save(comment);
        }
    }

    //delete - "/comments/{id}
    @Transactional
    public void deleteComment(Long commentId){
        Optional<Comment> comment = commentRepository.findById(commentId);
        System.out.println("Deleting comment with ID: " + commentId);
        if(comment.isPresent()){
            commentRepository.delete(comment.get());
            System.out.println("Comment deleted successfully.");
        }
    }
}