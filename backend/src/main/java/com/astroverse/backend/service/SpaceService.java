package com.astroverse.backend.service;

import com.astroverse.backend.model.Space;
import com.astroverse.backend.model.User;
import com.astroverse.backend.model.Post;
import com.astroverse.backend.model.UserSpace;
import com.astroverse.backend.repository.PostRepository;
import com.astroverse.backend.repository.SpaceRepository;
import com.astroverse.backend.repository.UserSpaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SpaceService {
    private final UserSpaceRepository userSpaceRepository;
    private final SpaceRepository spaceRepository;
    private final PostRepository postRepository;

    public SpaceService(SpaceRepository spaceRepository, UserSpaceRepository userSpaceRepository, PostRepository postRepository) {
        this.spaceRepository = spaceRepository;
        this.userSpaceRepository = userSpaceRepository;
        this.postRepository = postRepository;
    }

    public Space saveSpace(Space space) {
        return spaceRepository.save(space);
    }

    public int saveImage(long id, String image) {
        return spaceRepository.updateImageById(id, image);
    }
    public Optional<Space> getSpace(long id) {
        return spaceRepository.getSpaceById(id);
    }
    public int updateImage(long id, String image) {
        return spaceRepository.updateImageById(id, image);
    }
    public int updateSpace(long id, String titolo, String descrizione, String argomento) {
        return spaceRepository.updateSpaceDetailsById(id, titolo, descrizione, argomento);
    }

    public List<Space> searchSpace(String search) {
        return spaceRepository.findByTitleContainingIgnoreCaseOrArgumentContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, search);
    }

    public List<User> getUsersBySpace(Space space) {
        List<UserSpace> userSpaces = userSpaceRepository.findAllBySpace(space);
        List<User> users = new ArrayList<>();
        for (UserSpace userSpace : userSpaces) {
            users.add(userSpace.getUser());
        }
        return users;
    }

    public Long getAdmin(Space space) {
        UserSpace userSpace = userSpaceRepository.findFirstBySpaceAndIsSpaceAdmin(space, true);
        return userSpace.getUserId();
    }
    
    public Page<Post> getPost(Space space, int limit, int offset) {
        Pageable pageable = PageRequest.of(offset, limit);
        return postRepository.findAllBySpaceId(space.getId(), pageable);
    }


}