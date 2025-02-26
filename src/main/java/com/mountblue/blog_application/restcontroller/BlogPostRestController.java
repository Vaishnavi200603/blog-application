package com.mountblue.blog_application.restcontroller;

import com.mountblue.blog_application.dtos.PostDTO;
import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.model.Tag;
import com.mountblue.blog_application.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BlogPostRestController {
    private final PostService postService;

    public BlogPostRestController(PostService postService) {
        this.postService = postService;
    }

    //1. get all post api
    @GetMapping("/")
    public ResponseEntity<Page<PostDTO>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> author,
            @RequestParam(required = false) List<String> tagName,
            @RequestParam(required = false) List<String> publishedAt
    ) {
        Sort.Direction sortingDirection = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Page<Post> postPage = null;
        List<LocalDate> publishedDates = null;

        boolean isFilterApply = ((author != null && !author.isEmpty()) ||
                (publishedAt != null && !publishedAt.isEmpty()) ||
                (tagName != null && !tagName.isEmpty()) ||
                (search != null && !search.isEmpty()));

        if(isFilterApply){
            // Convert date strings to LocalDate
            publishedDates = new ArrayList<>();
            if (publishedAt != null) {
                for (String date : publishedAt) {
                    publishedDates.add(LocalDate.parse(date));
                }
            }

            postPage = postService.getFilteredPost(author, publishedDates, tagName, search,
                    PageRequest.of(page, size, Sort.by(sortingDirection, "publishedAt")));

        }
        else{
            postPage = postService.getAllPage(PageRequest.of(page, size, Sort.by(sortingDirection, "publishedAt")));
        }

        Page<PostDTO> responsePage = postPage.map(post -> new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getPublishedAt(),
                post.getTags().stream().map(Tag::getName).collect(Collectors.toList()) // Extract only tag names
        ));

        return ResponseEntity.ok(responsePage);

    }

    //2. show post as per id provided
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        Optional<Post> optionalPost = postService.getPostById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // Convert Post to PostDTO
            List<String> tagNames = new ArrayList<>();
            for (Tag tag : post.getTags()) {
                tagNames.add(tag.getName());
            }

            PostDTO postDTO = new PostDTO();
            postDTO.setId(post.getId());
            postDTO.setTitle(post.getTitle());
            postDTO.setAuthor(post.getAuthor());
            postDTO.setContent(post.getContent());
            postDTO.setTagNames(tagNames);

            return ResponseEntity.ok(postDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //3. create a new post
    @PostMapping("/create-post")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        postService.createAndSavePost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    //4. edit the previous post by id
    @PutMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or @postSecurityService.isPostOwner(#id, authentication.name)")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
        try {
            postService.updatePost(id, updatedPost);
            return ResponseEntity.ok("Post updated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //5. delete post by id
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @postSecurityService.isPostOwner(#id, authentication.name)")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok("Post deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Post not found!");
        }
    }
}
