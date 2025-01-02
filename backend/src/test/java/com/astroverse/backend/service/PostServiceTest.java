package com.astroverse.backend.service;

import com.astroverse.backend.model.Post;
import com.astroverse.backend.model.Space;
import com.astroverse.backend.model.User;
import com.astroverse.backend.repository.PostRepository;
import com.astroverse.backend.repository.SpaceRepository;
import com.astroverse.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SpaceRepository spaceRepository;
    @InjectMocks
    private PostService postService;
    @InjectMocks
    private Post post;
    @InjectMocks
    private UserService userService;
    @InjectMocks
    private SpaceService spaceService;

    @Test
    public void testSavePost() {
        User user = new User("prova", "prova", "provaUsername", "provatest@gmail.com", "provaPassword!22");
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        user = userService.saveUser(user);

        Space space = new Space("Scienze", "Fisica Terrestre", "Spazi dedito alla divulgazione scientifica");
        when(spaceRepository.save(space)).thenReturn(space);
        space = spaceService.saveSpace(space);

        post = new Post("Problema di Matematica: definisci il dominio di una funzione", space.getId(), user.getId());
        when(postRepository.save(post)).thenReturn(post);

        Post savedPost = postService.savePost(post);

        assertNotNull(savedPost);
        assertEquals("Problema di Matematica: definisci il dominio di una funzione", savedPost.getTesto());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void testUpdatePost() {
        User user = new User("prova", "prova", "provaUsername", "provatest@gmail.com", "provaPassword!22");
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        user = userService.saveUser(user);

        Space space = new Space("Scienze", "Fisica Terrestre", "Spazi dedito alla divulgazione scientifica");
        when(spaceRepository.save(space)).thenReturn(space);
        space = spaceService.saveSpace(space);

        post = new Post("Problema di Matematica: definisci il dominio di una funzione", space.getId(), user.getId());
        when(postRepository.save(post)).thenReturn(post);
        post = postService.savePost(post);

        post.setTesto("Problema di Matematica: risolvi l'integrale");
        when(postRepository.save(post)).thenReturn(post);
        post = postService.savePost(post);
        assertEquals(post.getTesto(), "Problema di Matematica: risolvi l'integrale");
    }
}