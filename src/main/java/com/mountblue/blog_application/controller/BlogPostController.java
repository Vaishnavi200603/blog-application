package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class BlogPostController {

    private final PostService postService;

    public BlogPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/create-post")
    public String showCreatePostForm(Model model){
        model.addAttribute("post", new Post());
        return "create-post";
    }

    @PostMapping("/newpost")
    public String createNewPost(@ModelAttribute Post post) {
        System.out.println("1. inside the createNewPost");
        postService.createAndSavePost(post);
        return "redirect:/";
    }


    //page = in which page
    //size = how many data per page
    @GetMapping("/")
    public String getAllPosts(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "4") int size,
                              @RequestParam(defaultValue = "desc") String sort,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String author,
                              @RequestParam(required = false) String publishedAt,
                              @RequestParam(required = false) List<String> tagName,
                              Model model) {

        System.out.println("First search : " + search);
        Page<Post> postPage; //this stores the data

        //sorting by asc and desc
        Sort.Direction sortingDirection = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sortingDirection, "publishedAt"));

        //1. check any filter is applied or not - take boolean
        boolean isFilterApply = ((author != null && !author.isEmpty()) ||
                (publishedAt != null && !publishedAt.isEmpty()) ||
                (tagName != null && !tagName.isEmpty()) ||
                (search != null && !search.isEmpty()));

        //2. if yes
        if(isFilterApply){
            System.out.println(tagName);
            LocalDateTime startDay = null;
            LocalDateTime endDay = null;
            if(publishedAt != null && !publishedAt.isEmpty()){
                startDay = LocalDate.parse(publishedAt).atStartOfDay();
                endDay = startDay.plusDays(1);
            }
            //fetch according to filter applied
            postPage = postService.getFilteredPost(author, startDay, endDay, tagName, search, PageRequest.of(page, size, Sort.by(sortingDirection, "publishedAt")));
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

//        System.out.println(allTagNames);
        // Add attributes to the model
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("sort", sort);
        model.addAttribute("author", author);
        model.addAttribute("publishedAt", publishedAt);
        model.addAttribute("tagName", tagName);
        model.addAttribute("search", search);
        model.addAttribute("isFilterApplied", isFilterApply);

        // ✅ Add Dropdown Data to Model
        model.addAttribute("authors", authors);
        model.addAttribute("tagNames", allTagNames);
        model.addAttribute("dates", dates);


        return "all-posts";
    }




//    http://localhost:8080/posts?page=1&size=5 -- pagination
//    http://localhost:8080/ -- start





}
