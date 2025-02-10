package com.mountblue.blog_application.service;

import com.mountblue.blog_application.model.Posts;
import com.mountblue.blog_application.model.Tags;
import com.mountblue.blog_application.repository.PostRepository;
import com.mountblue.blog_application.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {
    //final keyword ensure that it cannot be reassigned after initialization.
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public void createAndSavePost(Posts post) {
        String excerpt = generateExcerpt(post.getContent());
        post.setExcerpt(excerpt);

        post.setAuthor("Namritha Thapar");
        post.setPublishedAt(LocalDateTime.now());
        post.setPublished(true);

        Set<Tags> tagSet = processTags(post.getTagNames());
        post.setTags(tagSet);

        postRepository.save(post);
    }

    private String generateExcerpt(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }
        String[] words = content.trim().split("\\s+"); //this \\s+ means Splits a string by whitespace (spaces, tabs, new lines, etc.).
        if (words.length > 50) {
            return String.join(" ", Arrays.asList(words).subList(0, 20)) + "...";
        } else {
            return content;
        }
    }

    private Set<Tags> processTags(String tagNames) {
        Set<Tags> tagSet = new HashSet<>();
        if (tagNames != null && !tagNames.trim().isEmpty()) {
            String[] tagArray = tagNames.split(",");
            for (String tagName : tagArray) {
                tagName = tagName.trim();
                Tags tag = tagRepository.findByName(tagName);
                if (tag == null) {
                    tag = new Tags();
                    tag.setName(tagName);
                    tag = tagRepository.save(tag);
                }
                tagSet.add(tag);
            }
        }
        return tagSet;
    }

    public List<Posts> getAllPublishedPosts(){
        return postRepository.findByIsPublishedTrueOrderByPublishedAtDesc();
    }

    public Posts getPostById(Long id){
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
    }
}
