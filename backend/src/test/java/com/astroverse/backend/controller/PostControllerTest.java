package com.astroverse.backend.controller;

import com.astroverse.backend.model.Post;
import com.astroverse.backend.model.Space;
import com.astroverse.backend.model.User;
import com.astroverse.backend.repository.SpaceRepository;
import com.astroverse.backend.repository.UserRepository;
import com.astroverse.backend.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {
    @Mock
    private PostService postService;
    @InjectMocks
    private PostController postController;
    @Mock
    private SpaceRepository spaceRepository;
    @Mock
    private UserRepository userRepository;
    private final String exampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIERhdGEiLCJpZCI6MSwiZW1haWwiOiJwcm92YXRlc3RAZ21haWwuY29tIiwidXNlcm5hbWUiOiJwcm92YVVzZXJuYW1lIiwibm9tZSI6InByb3ZhIiwiY29nbm9tZSI6InByb3ZhIiwiaXNBZG1pbiI6ZmFsc2UsImlhdCI6MTczNTkwMDQ0MywiaXNzIjoiQXN0cm92ZXJzZSIsImV4cCI6MTczODQ5MjQ0M30.KOOIxhlzWj-UdhbcscVB1CXoNAd6lsxfOFKRt3diYvk";

    @Test
    public void testCreatePost() {
        Space space = new Space("Titolo", "Argomento", "Descrizione");

        when(spaceRepository.save(space)).thenReturn(space);
        space = spaceRepository.save(space);

        User user = new User("prova", "prova", "provaUsername", "provatest@gmail.com", "provaPassword!22");

        when(userRepository.save(user)).thenReturn(user);
        user = userRepository.save(user);

        Post post = new Post("ProvaTesto", space.getId(), user);

        when(postService.savePost(any(Post.class))).thenReturn(post);
        ResponseEntity<?> response = postController.createPost(post.getTesto(), null, exampleToken, space.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testModifyPost() {
        Space space = new Space("Titolo", "Argomento", "Descrizione");

        when(spaceRepository.save(space)).thenReturn(space);
        space = spaceRepository.save(space);

        User user = new User("prova", "prova", "provaUsername", "provatest@gmail.com", "provaPassword!22");

        when(userRepository.save(user)).thenReturn(user);
        user = userRepository.save(user);

        Post post = new Post("ProvaTesto", space.getId(), user);

        when(postService.savePost(any(Post.class))).thenReturn(post);
        ResponseEntity<?> response = postController.createPost(post.getTesto(), null, exampleToken, space.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        post.setTesto("TestoModificato");

        when(postService.isCreationUser(anyLong(), anyLong())).thenReturn(true);
        when(postService.getPost(space.getId())).thenReturn(post);
        when(postService.savePost(post)).thenReturn(post);
        ResponseEntity<?> responseModify = postController.modifyPost(space.getId(), post.getTesto(), null, exampleToken);
        assertEquals(HttpStatus.OK, responseModify.getStatusCode());
    }
}