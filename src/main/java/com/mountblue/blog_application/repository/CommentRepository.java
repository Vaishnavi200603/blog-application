package com.mountblue.blog_application.repository;

import com.mountblue.blog_application.model.Comment;
import com.mountblue.blog_application.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndParentCommentIsNull(Long postId);
}
