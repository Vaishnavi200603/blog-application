package com.mountblue.blog_application.repository;

import com.mountblue.blog_application.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Posts, Long> {
    List<Posts> findByIsPublishedTrueOrderByPublishedAtDesc();

}
