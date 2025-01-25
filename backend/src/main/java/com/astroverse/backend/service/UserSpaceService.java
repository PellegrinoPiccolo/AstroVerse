package com.astroverse.backend.service;

import com.astroverse.backend.model.Space;
import com.astroverse.backend.model.User;
import com.astroverse.backend.model.UserSpace;
import com.astroverse.backend.repository.UserSpaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public int deleteUserSpace(UserSpace userSpace) {
        return userSpaceRepository.deleteByUser_IdAndSpace_Id(userSpace.getUser().getId(), userSpace.getSpace().getId());
    }

    public long getNumberOfUsers(Space space) {
        return userSpaceRepository.countBySpace(space);
    }

    public Page<UserSpace> getAllUserBySpace(Space space, int limit, int offset) {
        Pageable pageable = PageRequest.of(offset, limit);
        return userSpaceRepository.findAllBySpace(space, pageable);
    }
}