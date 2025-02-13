package com.mountblue.blog_application.service;

import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.model.Tag;
import com.mountblue.blog_application.repository.PostRepository;
import com.mountblue.blog_application.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
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

    public List<Post> getAllPublishedPosts() {
        return postRepository.findByIsPublishedTrueOrderByPublishedAtDesc();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public void updatePost(Long id, Post updatedPost) {
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

    public Page<Post> getAllPage(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<Post> getFilteredPost(String author, LocalDateTime startDay, LocalDateTime endDay,
                                      List<String> tagNames, String search, Pageable pageable) {

        //1. for searching
        if (search != null && !search.isEmpty()) {
            System.out.println("SEARCH : " + search);
            if(!search.equals(",")) return postRepository.searchPosts(search, pageable);
        }

        //2. if only date was given
        if((author == null || author.isEmpty()) && (tagNames == null || tagNames.isEmpty())){
            return postRepository.findByPublishedAtBetween(startDay, endDay, pageable);
        }

        //3. only author was given
        else if(startDay == null && (tagNames == null || tagNames.isEmpty())){
            return postRepository.findByAuthorIgnoreCase(author, pageable);
        }

        //4. if only author and tags were given
        else if(tagNames == null || tagNames.isEmpty()){
            return postRepository.findByAuthorIgnoreCaseAndPublishedAtBetween(author, startDay, endDay, pageable);
        }

        //5. if only tags were given
        else if((author == null || author.isEmpty()) && startDay == null){
            return postRepository.findByTags_NameIn(tagNames, pageable);
        }

        //6. if only author and tags were given
        else if (startDay == null) {
            return postRepository.findByAuthorIgnoreCaseAndTags_NameIn(author, tagNames, pageable);
        }

        //7. if only date and tags were given
        else if (author == null || author.isEmpty()) {
            return postRepository.findByPublishedAtBetweenAndTags_NameIn(startDay, endDay, tagNames, pageable);
        }

        //8. if everything were given
        else {
            return postRepository.findByAuthorIgnoreCaseAndPublishedAtBetweenAndTags_NameIn(author, startDay, endDay, tagNames, pageable);
        }



        //        return postRepository.findFilteredPosts(author, startDay, endDay, tag, pageable);


    }

    public List<String> getAllAuthors() {
        return postRepository.findDistinctAuthors();

    }

    public List<LocalDate> getAllPublishedDates() {
        List<java.sql.Date> dates = postRepository.findDistinctPublishedDates();

        List<LocalDate> localDates = new ArrayList<>();
        for (java.sql.Date sqlDate : dates) {
            localDates.add(sqlDate.toLocalDate());  // Convert java.sql.Date → LocalDate
        }

        return localDates;
    }

    public List<String> getAllTagNames(){
        List<Tag> allTags = tagRepository.findAll();
        List<String> tagNames = new ArrayList<>();

        for(Tag tag : allTags){
            tagNames.add(tag.getName());
        }
        return tagNames;
    }
}


