package com.mountblue.blog_application.dtos;

import com.mountblue.blog_application.model.Tag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime publishedAt;
    private List<String> tagNames;

    public PostDTO() {
    }

    public PostDTO(Long id, String title, String content,
                   String author, LocalDateTime publishedAt, List<String> tagNames) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.publishedAt = publishedAt;
        this.tagNames = tagNames;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }
}

