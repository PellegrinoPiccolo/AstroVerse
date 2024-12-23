package com.astroverse.backend.service;

import com.astroverse.backend.model.Post;
import com.astroverse.backend.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post savePost(Post post) {return postRepository.save(post);}
}