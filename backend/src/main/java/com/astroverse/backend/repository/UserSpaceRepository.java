package com.astroverse.backend.repository;

import com.astroverse.backend.model.UserSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSpaceRepository extends JpaRepository<UserSpace, Long> {
    boolean existsByUser_IdAndSpace_Id(Long userId, Long spaceId);
    boolean existsByUser_IdAndSpace_IdAndIsSpaceAdminTrue(Long userId, Long spaceId); // Metodo per verificare se è admin
}