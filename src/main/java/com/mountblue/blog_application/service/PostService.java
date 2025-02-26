package com.mountblue.blog_application.service;

import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.model.Tag;
import com.mountblue.blog_application.model.User;
import com.mountblue.blog_application.repository.PostRepository;
import com.mountblue.blog_application.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository, TagRepository tagRepository, UserService userService) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public void createAndSavePost(Post post) {
        String excerpt = generateExcerpt(post.getContent());
        post.setExcerpt(excerpt);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User is not authenticated!");
        }

        String userEmail = auth.getName();
        User currentUser = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        post.setAuthorDetails(currentUser);
        post.setAuthor(currentUser.getDisplayName()); // Assign author's name
        post.setPublishedAt(LocalDateTime.now());
        post.setPublished(true);

        Set<Tag> tagSet = processTags(post.getTagNames());
        post.setTags(tagSet);

        postRepository.save(post);
    }

    public String generateExcerpt(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }
        String[] words = content.trim().split("\\s+"); //this \\s+ means Splits a string by whitespace (spaces, tabs, new lines, etc.).
        if (words.length > 20) {
            return String.join(" ", Arrays.asList(words).subList(0, 20)) + "...";
        } else {
            return content;
        }
    }

    public Set<Tag> processTags(String tagNames) {
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

    @PreAuthorize("hasRole('ADMIN') or (hasRole('AUTHOR') and @postRepository.findById(#id).get().authorDetails.email == authentication.name)")
    public void updatePost(Long id, Post updatedPost) {
        // 1. Get the currently authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal(); // Get the principal

        User currentUser;

        if (principal instanceof User) {
            // Case 1: If it's an instance of our custom User entity
            currentUser = (User) principal;
        } else if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            // Case 2: If it's Spring Security's User, fetch from the database
            Optional<User> optionalUser = userService.findByEmail(springUser.getUsername());
            if (optionalUser.isEmpty()) {
                throw new RuntimeException("User not found!");
            }
            currentUser = optionalUser.get();
        } else {
            throw new RuntimeException("Authentication error: Invalid user type.");
        }

        // 2. Fetch the existing post
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            throw new RuntimeException("Post not found!");
        }
        Post existingPost = postOptional.get();

        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setPublishedAt(LocalDateTime.now());

        // 5. Update tags
        Set<Tag> tagsSet = processTags(updatedPost.getTagNames());
        existingPost.setTags(tagsSet);

        // 6. Update content and generate excerpt
        existingPost.setContent(updatedPost.getContent());
        String excerpt = generateExcerpt(updatedPost.getContent());
        existingPost.setExcerpt(excerpt);

        // 7. Save the updated post
        postRepository.save(existingPost);
    }

    //before going deleting and updating post that has role author, it must first check currecntly logged user is equal to post author
    //author can only delete or update his own post
//    @PreAuthorize("hasRole('ADMIN') or (hasRole('AUTHOR') and #post.authorDetails.email == authentication.name)")
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

    public Page<Post> getFilteredPost(List<String> authors, List<LocalDate> publishedDates,
                                      List<String> tagNames, String search, Pageable pageable) {
        List<LocalDateTime> publishedDateTimes = new ArrayList<>();
        for (LocalDate date : publishedDates) {
            publishedDateTimes.add(date.atStartOfDay());
        }

        //1. for searching
        if (search != null && !search.isEmpty()) {
            return postRepository.searchPosts(search, pageable);
        }

        //2. if only date was given
        if((authors == null || authors.isEmpty()) && (tagNames == null || tagNames.isEmpty())){
            return postRepository.findByPublishedAtIn(publishedDateTimes, pageable);
        }

        //3. only author was given
        else if(publishedDateTimes.isEmpty() && (tagNames == null || tagNames.isEmpty())){
            return postRepository.findByAuthorInIgnoreCase(authors, pageable);
        }

        //4. if only author and publishedDate were given
        else if (tagNames == null || tagNames.isEmpty()) {
            return postRepository.findByAuthorInIgnoreCaseAndPublishedAtIn(authors, publishedDateTimes, pageable);
        }

        //5. if only tags were given
        else if((authors == null || authors.isEmpty()) && publishedDateTimes.isEmpty()){
            return postRepository.findByTags_NameIn(tagNames, pageable);
        }

        //6. if only author and tags were given
        else if (publishedDateTimes.isEmpty()) {
            return postRepository.findByAuthorInIgnoreCaseAndTags_NameIn(authors, tagNames, pageable);
        }

        //7. if only date and tags were given
        else if (authors == null || authors.isEmpty()) {
            return postRepository.findByPublishedAtInAndTags_NameIn(publishedDateTimes, tagNames, pageable);
        }

        //8. if everything were given
        else {
            return postRepository.findByAuthorInIgnoreCaseAndPublishedAtInAndTags_NameIn(authors, publishedDateTimes, tagNames, pageable);
        }
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

    public List<String> getAllTagNames() {
        List<Tag> allTags = tagRepository.findAll();
        List<String> tagNames = new ArrayList<>();
        for (Tag tag : allTags) {
            tagNames.add(tag.getName());
        }
        return tagNames;
    }


}


