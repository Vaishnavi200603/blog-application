package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BlogPostController {

    private final PostService postService;

    public BlogPostController(PostService postService) {
        this.postService = postService;
    }

    //model
    // interface in spring mvc
    // let controllers takes dynamic content to the view layer
    // acts as container both of them

    @GetMapping("/create-post")
    public String showCreatePostForm(Model model){
        model.addAttribute("post", new Post());
        return "create-post";
    }

    // model attribute
    // when used as parameter - bind request parameter of form data to the java object properties
    // used in controller for capture and convert incoming data into model attribute

    @PostMapping("/newpost")
    public String createNewPost(@ModelAttribute Post post) {;
        postService.createAndSavePost(post);
        return "redirect:/";
    }

    //page = in which page
    //size = how many data per page
    @GetMapping("/")
    public String getAllPosts(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "6") int size,
                              @RequestParam(defaultValue = "desc") String sort,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) List<String> author,
                              @RequestParam(required = false) List<String> publishedAt,
                              @RequestParam(required = false) List<String> tagName,
                              Model model) {

        Page<Post> postPage;
        List<LocalDate> publishedDates = null;

        System.out.println("SORT : " + sort);

        //sorting by asc and desc
        Sort.Direction sortingDirection = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        //1. check any filter is applied or not - take boolean
        boolean isFilterApply = ((author != null && !author.isEmpty()) ||
                (publishedAt != null && !publishedAt.isEmpty()) ||
                (tagName != null && !tagName.isEmpty()) ||
                (search != null && !search.isEmpty()));

        //2. if yes
        if (isFilterApply) {
            publishedDates = new ArrayList<>();

            if (publishedAt != null && !publishedAt.isEmpty()) {
                for (String date : publishedAt) {
                    date = date.replaceAll("[\\[\\]]", "");  // Remove all square brackets
                    date = date.trim(); // Remove spaces
                    publishedDates.add(LocalDate.parse(date));
                }
            }

            System.out.println("Published At : " + publishedDates);
            System.out.println("Author : " + author);

            postPage = postService.getFilteredPost(author, publishedDates, tagName, search,
                    PageRequest.of(page, size, Sort.by(sortingDirection, "publishedAt")));
        }

        else{ //3. if no - filter is not applied
            postPage = postService.getAllPage(PageRequest.of(page, size, Sort.by(sortingDirection, "publishedAt")));
        }

        //4. to get data for dropdown menu
        List<String> authors = postService.getAllAuthors();  // Fetch all authors for dropdown
        List<String> allTagNames = postService.getAllTagNames();
        List<LocalDate> dates = postService.getAllPublishedDates();// Fetch all unique published dates

        System.out.println("SORT : " + sort);
        System.out.println("TAG-NAME : " + tagName);
        System.out.println("PUBLISHED AT : " + publishedAt);

//        System.out.println(allTagNames);
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
//    http://localhost:8080/posts?page=1&size=5 -- pagination
//    http://localhost:8080/ -- start
}
