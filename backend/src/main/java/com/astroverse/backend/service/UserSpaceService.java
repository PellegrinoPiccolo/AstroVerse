package com.astroverse.backend.service;

import com.astroverse.backend.model.Space;
import com.astroverse.backend.model.User;
import com.astroverse.backend.model.UserSpace;
import com.astroverse.backend.repository.UserSpaceRepository;
import org.springframework.stereotype.Service;

@Service
public class UserSpaceService {
    private final UserSpaceRepository userSpaceRepository;

    public UserSpaceService(UserSpaceRepository userSpaceRepository) {
        this.userSpaceRepository = userSpaceRepository;
    }

    public UserSpace saveUserSpace(UserSpace userSpace) {
        return userSpaceRepository.save(userSpace);
    }

    public boolean existSubscribe(UserSpace userSpace) {
        User user = userSpace.getUser();
        Space space = userSpace.getSpace();
        return userSpaceRepository.existsByUser_IdAndSpace_Id(user.getId(), space.getId());
    }

    public boolean isUserAdmin(UserSpace userSpace) {
        User user = userSpace.getUser();
        Space space = userSpace.getSpace();
        return userSpaceRepository.existsByUser_IdAndSpace_IdAndIsSpaceAdminTrue(user.getId(), space.getId());
    }

    public UserSpace saveUserSpaceAdmin(UserSpace userSpace) {
        userSpace.setSpaceAdmin(true);
        return userSpaceRepository.save(userSpace);
    }
}