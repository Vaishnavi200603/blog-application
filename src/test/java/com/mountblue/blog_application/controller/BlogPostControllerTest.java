package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.service.CommentService;
import com.mountblue.blog_application.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BlogPostController.class)
public class BlogPostControllerTest {

    @Autowired
    MockMvc mockMvc; //Spring Boot tool that simulates HTTP requests.

    @MockitoBean
    PostService postService; //we just call the service but return mock result


    private CommentService commentService;

    @Test
    @WithMockUser(username = "author", roles = {"AUTHOR"})
    void shouldReturnCreatePostFormForAuthor() throws Exception {
        mockMvc.perform(get("/create-post"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-post"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldReturnCreatePostFormForAdmin() throws Exception {
        mockMvc.perform(get("/create-post"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-post"));
    }

//    @Test
//    void shouldRedirectToLoginIfNotAuthenticated() throws Exception {
//        mockMvc.perform(get("/create-post").with(anonymous()))  // Simulate an unauthenticated user
//                .andExpect(status().is3xxRedirection())  // Expect redirection (302)
//                .andExpect(redirectedUrl("/login"));  // Remove 'http://localhost'
//    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})  // User has no valid role
    void shouldRedirectIfUserIsNotAuthorOrAdmin() throws Exception {
        mockMvc.perform(get("/create-post"))
                .andExpect(status().is3xxRedirection())  // Expect redirect (302)
                .andExpect(redirectedUrl("/"));  // Redirects unauthorized users to login
    }
}
