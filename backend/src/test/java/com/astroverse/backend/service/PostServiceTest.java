package com.astroverse.backend.service;

import com.astroverse.backend.model.Post;
import com.astroverse.backend.repository.PostRepository;
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
    @InjectMocks
    private PostService PostService;
    @InjectMocks
    private Post post;

    @Test
    public void testSavePost() {
        post = new Post("Problema di Matematica: definisci il dominio di una funzione", 1, 1);
        when(postRepository.save(post)).thenReturn(post);

        Post savedPost = PostService.savePost(post);

        assertNotNull(savedPost);
        assertEquals("Problema di Matematica: definisci il dominio di una funzione", savedPost.getTesto());
        assertEquals(1, savedPost.getSpaceId());
        assertEquals(1, savedPost.getUserId());
        verify(postRepository, times(1)).save(post);
    }
}