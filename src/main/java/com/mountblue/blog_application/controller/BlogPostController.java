package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.model.User;
import com.mountblue.blog_application.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BlogPostController {

    private final PostService postService;

    public BlogPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/create-post")
    public String showCreatePostForm(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());

        model.addAttribute("isAuthenticated", isAuthenticated);
        if (isAuthenticated && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            model.addAttribute("displayName", user.getName());
        } else {
            model.addAttribute("displayName", "Guest");
        }

        //checked if role is author only
        boolean isAuthor = false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_AUTHOR") || authority.getAuthority().equals("ROLE_ADMIN")) {
                isAuthor = true;
                break;
            }
        }

        if (!isAuthor) {
            return "redirect:/";
        }
        model.addAttribute("post", new Post());
        return "create-post";
    }

    @PostMapping("/newpost")
    public String createNewPost(@ModelAttribute Post post) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();

            if (auth.isAuthenticated() && auth.getPrincipal() instanceof User user) {
                post.setAuthor(user.getName());
            }
        }

        postService.createAndSavePost(post);
        return "redirect:/";
    }

    @GetMapping("/")
    public String getAllPosts(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(defaultValue = "desc") String sort,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) List<String> author,
                              @RequestParam(required = false) List<String> publishedAt,
                              @RequestParam(required = false) List<String> tagName,
                              Model model) {

        Page<Post> postPage;
        List<LocalDate> publishedDates = null;

        Sort.Direction sortingDirection = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        boolean isFilterApply = ((author != null && !author.isEmpty()) ||
                (publishedAt != null && !publishedAt.isEmpty()) ||
                (tagName != null && !tagName.isEmpty()) ||
                (search != null && !search.isEmpty()));

        if (isFilterApply) {
            publishedDates = new ArrayList<>();
            if (publishedAt != null && !publishedAt.isEmpty()) {
                for (String date : publishedAt) {
                    date = date.replaceAll("[\\[\\]]", "");  // Remove all square brackets
                    date = date.trim(); // Remove spaces
                    publishedDates.add(LocalDate.parse(date));
                }
            }

            if (author != null && !author.isEmpty()) {
                List<String> processedAuthors = new ArrayList<>();

                for (String authorName : author) {
                    authorName = authorName.trim();  // Remove extra spaces
                    authorName = authorName.replaceAll("[\\[\\]]", "");  // Remove square brackets if present
                    processedAuthors.add(authorName);
                }
                author = processedAuthors; // Assign the processed list back to `author`
                System.out.println("Processed Authors: " + author);
            }
            System.out.println("Published At : " + publishedDates);
            System.out.println("Author : " + author);

            postPage = postService.getFilteredPost(author, publishedDates, tagName, search,
                    PageRequest.of(page, size, Sort.by(sortingDirection, "publishedAt")));
        } else{
            postPage = postService.getAllPage(PageRequest.of(page, size, Sort.by(sortingDirection, "publishedAt")));
        }

        //dropdown
        List<String> authors = postService.getAllAuthors();
        List<String> allTagNames = postService.getAllTagNames();
        List<LocalDate> dates = postService.getAllPublishedDates();

        System.out.println("SORT : " + sort);
        System.out.println("TAG-NAME : " + tagName);
        System.out.println("PUBLISHED AT : " + publishedAt);

        // Add attributes to the model
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("sort", sort);
        model.addAttribute("author", author);
        model.addAttribute("publishedAt", publishedDates);
        model.addAttribute("tagName", tagName);
        model.addAttribute("search", search);
        model.addAttribute("isFilterApplied", isFilterApply);

        // dropdown data to model
        model.addAttribute("authors", authors);
        model.addAttribute("tagNames", allTagNames);
        model.addAttribute("dates", dates);

        return "all-posts";
    }
}
