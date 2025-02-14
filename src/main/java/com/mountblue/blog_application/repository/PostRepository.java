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
//    List<Post> findByAuthor(String author);
//    List<Post> findByPublishedAt(LocalDateTime publishedAt);


    //1. for searching
    @Query("SELECT p FROM Post p LEFT JOIN p.tags t " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.author) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Post> searchPosts(@Param("query") String query, Pageable pageable);

    //2. if only date were given
    @Query("SELECT p FROM Post p WHERE DATE(p.publishedAt) IN :publishedDateTimes")
    Page<Post> findByPublishedAtIn(@Param("publishedDateTimes") List<LocalDateTime> publishedDateTimes, Pageable pageable);

    //3. if only authors were given
    Page<Post> findByAuthorInIgnoreCase(List<String> authors, Pageable pageable);

    //4. if only author and publishedDate were given
//    Page<Post> findByAuthorInIgnoreCaseAndPublishedAtIn(List<String> authors, List<LocalDateTime> publishedDates, Pageable pageable);
    @Query("SELECT p FROM Post p " +
            "WHERE p.author IN :authors " +
            "AND DATE(p.publishedAt) IN :publishedDates")
    Page<Post> findByAuthorInIgnoreCaseAndPublishedAtIn(@Param("authors") List<String> authors,
                                                        @Param("publishedDates") List<LocalDateTime> publishedDates,
                                                        Pageable pageable);

    //5. if only tag names were given
    Page<Post> findByTags_NameIn(List<String> tagNames, Pageable pageable);

    //6. if only authors and tag names were given
    Page<Post> findByAuthorInIgnoreCaseAndTags_NameIn(List<String> author, List<String> tagNames, Pageable pageable);

    //7. if only dates and tags were given
//    Page<Post> findByPublishedAtInAndTags_NameIn(List<LocalDateTime> publishedDates, List<String> tagNames, Pageable pageable);
    @Query("SELECT p FROM Post p JOIN p.tags t " +
            "WHERE DATE(p.publishedAt) IN :publishedDates " +
            "AND t.name IN :tagNames")
    Page<Post> findByPublishedAtInAndTags_NameIn(@Param("publishedDates") List<LocalDateTime> publishedDates,
                                                 @Param("tagNames") List<String> tagNames,
                                                 Pageable pageable);

    //8. if all were given
//    Page<Post> findByAuthorInIgnoreCaseAndPublishedAtInAndTags_NameIn(List<String> author, List<LocalDateTime> publishedDates, List<String> tagNames, Pageable pageable);
    @Query("SELECT p FROM Post p JOIN p.tags t " +
            "WHERE p.author IN :authors " +
            "AND DATE(p.publishedAt) IN :publishedDates " +
            "AND t.name IN :tagNames")
    Page<Post> findByAuthorInIgnoreCaseAndPublishedAtInAndTags_NameIn(@Param("authors") List<String> authors,
                                                                      @Param("publishedDates") List<LocalDateTime> publishedDates,
                                                                      @Param("tagNames") List<String> tagNames,
                                                                      Pageable pageable);


    //for dropdown menu
    // Get unique authors
    @Query("SELECT DISTINCT " +
            "p.author FROM Post p")
    List<String> findDistinctAuthors();

    // Get unique published dates
    @Query("SELECT DISTINCT " +
            "DATE(p.publishedAt) " +
            "FROM Post p")
    List<java.sql.Date> findDistinctPublishedDates();


}
