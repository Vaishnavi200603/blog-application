package com.mountblue.blog_application.service;

import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.model.Tag;
import com.mountblue.blog_application.repository.PostRepository;
import com.mountblue.blog_application.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

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

    public void createAndSavePost(Post post) {
        String excerpt = generateExcerpt(post.getContent());
        post.setExcerpt(excerpt);

//        post.setAuthor("Namritha Thapar");
        post.setPublishedAt(LocalDateTime.now());
        post.setPublished(true);
        post.setTagNames(post.getTagNames());
        System.out.println("While Setting first : " + post.getTagNames());

        Set<Tag> tagSet = processTags(post.getTagNames());
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

    private Set<Tag> processTags(String tagNames) {
        Set<Tag> tagSet = new HashSet<>();
        if (tagNames != null && !tagNames.trim().isEmpty()) {
            String[] tagArray = tagNames.split(",");
            for (String tagName : tagArray) {
                tagName = tagName.trim();
                Tag tag = tagRepository.findByName(tagName);
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(tagName);
                    tag = tagRepository.save(tag);
                }
                tagSet.add(tag);
            }
        }
        return tagSet;
    }

    public List<Post> getAllPublishedPosts(){
        return postRepository.findByIsPublishedTrueOrderByPublishedAtDesc();
    }

    public Optional<Post> getPostById(Long id){
        return postRepository.findById(id);
    }

    public void updatePost(Long id, Post updatedPost){
        Optional<Post> post = postRepository.findById(id);
        System.out.println("Updated Posts : " + updatedPost.getTagNames());

        Post existingPost = post.get();

        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setPublishedAt(LocalDateTime.now());
        Set<Tag> tagsSet = processTags(updatedPost.getTagNames());
        existingPost.setTags(tagsSet);
//        existingPost.setTags(updatedPost.getTags());
        existingPost.setContent(updatedPost.getContent());
        String excerpt = generateExcerpt(updatedPost.getContent());
        existingPost.setExcerpt(excerpt);
        postRepository.save(existingPost);

    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public String setTagsNameFromTags(Long id) {
        Optional<Post> post = postRepository.findById(id);

        if (post.isPresent()) {
            Set<Tag> tags = post.get().getTags();

            StringBuilder tagNames = new StringBuilder();

            for (Tag tag : tags) {
                if (!tagNames.isEmpty()) {
                    tagNames.append(", ");
                }
                tagNames.append(tag.getName());
            }

            System.out.println("Converted Tag Names: " + tagNames);
            return tagNames.toString();
        } else {
            return "Post not found!";
        }
    }

}
