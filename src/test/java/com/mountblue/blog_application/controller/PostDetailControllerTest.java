package com.mountblue.blog_application.controller;

import com.mountblue.blog_application.model.Comment;
import com.mountblue.blog_application.model.Post;
import com.mountblue.blog_application.model.User;
import com.mountblue.blog_application.service.CommentService;
import com.mountblue.blog_application.service.PostService;
import com.mountblue.blog_application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostDetailControllerTest {
    @Mock
    PostService postService;

    @Mock
    CommentService commentService;

    @Mock
    UserService userService;

    @InjectMocks
    PostDetailController postDetailController;

    @Mock
    Principal principal;

    @Mock
    Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDetailsOfPost_ExistingPost(){
        Long postId = 1L;
        Post mockPost = new Post();
        mockPost.setId(postId);
        Comment mockComment = new Comment();
        List<Comment> comments = Arrays.asList(mockComment);
        User mockUser = new User();
        mockUser.setName("John Doe");
        mockUser.setEmail("john@example.com");

        when(postService.getPostById(postId)).thenReturn(Optional.of(mockPost));
        when(commentService.getCommentsByPost(postId)).thenReturn(comments);
        when(principal.getName()).thenReturn("john@example.com");
        when(userService.getUserByEmail("john@example.com")).thenReturn(mockUser);

        // When
        String viewName = postDetailController.getDetailOfPost(postId, model, principal);

        // Then
        verify(model).addAttribute("post", mockPost);
        verify(model).addAttribute("comments", comments);
        verify(model).addAttribute(eq("newComment"), any(Comment.class));
        assertEquals("post-details", viewName);
    }

    @Test
    void testGetDetailOfPost_NonExistingPost() {
        // Given
        Long postId = 1L;
        when(postService.getPostById(postId)).thenReturn(Optional.empty());

        // When
        String viewName = postDetailController.getDetailOfPost(postId, model, principal);

        // Then
        assertEquals("error-page", viewName);
    }

    @Test
    void testUpdatePost_ExistingPost() {
        // Given
        Long postId = 1L;
        Post mockPost = new Post();
        when(postService.getPostById(postId)).thenReturn(Optional.of(mockPost));
        when(postService.setTagsNameFromTags(postId)).thenReturn("Spring Boot, Thymeleaf");

        // When
        String viewName = postDetailController.updatePost(postId, model, principal);

        // Then
        verify(model).addAttribute("post", mockPost);
        assertEquals("update-post", viewName);
    }

    @Test
    void testUpdatePost_NonExistingPost() {
        // Given
        Long postId = 1L;
        when(postService.getPostById(postId)).thenReturn(Optional.empty());

        // When
        String viewName = postDetailController.updatePost(postId, model, principal);

        // Then
        assertEquals("redirect:/error", viewName);
    }

    @Test
    void testShowUpdatedPost() {
        // Given
        Long postId = 1L;
        Post updatedPost = new Post();

        // When
        String viewName = postDetailController.showUpdatedPost(postId, updatedPost);

        // Then
        verify(postService).updatePost(postId, updatedPost);
        assertEquals("redirect:/post/" + postId, viewName);
    }

    @Test
    void testDeletePost() {
        // Given
        Long postId = 1L;

        // When
        String viewName = postDetailController.deletePost(postId);

        // Then
        verify(postService).deletePost(postId);
        assertEquals("redirect:/", viewName);
    }
}
