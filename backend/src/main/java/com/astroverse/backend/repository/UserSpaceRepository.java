package com.astroverse.backend.repository;

import com.astroverse.backend.model.UserSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserSpaceRepository extends JpaRepository<UserSpace, Long> {
    boolean existsByUser_IdAndSpace_Id(Long userId, Long spaceId);
}