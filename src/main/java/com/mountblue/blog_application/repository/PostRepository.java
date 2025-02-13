package com.mountblue.blog_application.repository;

import com.mountblue.blog_application.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByIsPublishedTrueOrderByPublishedAtDesc();
    List<Post> findByAuthor(String author);
    List<Post> findByPublishedAt(LocalDateTime publishedAt);

    Page<Post> findByAuthorIgnoreCase(String author, Pageable pageable);

    Page<Post> findByPublishedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Post> findByAuthorIgnoreCaseAndPublishedAtBetween(String author, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Post> findByTags_NameIn(List<String> tagNames, Pageable pageable);

    Page<Post> findByAuthorIgnoreCaseAndTags_NameIn(String author, List<String> tagNames, Pageable pageable);

    Page<Post> findByPublishedAtBetweenAndTags_NameIn(LocalDateTime start, LocalDateTime end, List<String> tagNames, Pageable pageable);

    Page<Post> findByAuthorIgnoreCaseAndPublishedAtBetweenAndTags_NameIn(String author, LocalDateTime start, LocalDateTime end, List<String> tagNames, Pageable pageable);

    // Get unique authors
    @Query("SELECT DISTINCT " +
            "p.author FROM Post p")
    List<String> findDistinctAuthors();

    // Get unique published dates
    @Query("SELECT DISTINCT " +
            "DATE(p.publishedAt) " +
            "FROM Post p")
    List<java.sql.Date> findDistinctPublishedDates();

    //for searching
    @Query("SELECT p FROM Post p LEFT JOIN p.tags t " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.author) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Post> searchPosts(@Param("query") String query, Pageable pageable);
}
