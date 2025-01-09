package com.astroverse.backend.repository;

import com.astroverse.backend.model.Space;
import com.astroverse.backend.model.User;
import com.astroverse.backend.model.UserSpace;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface UserSpaceRepository extends JpaRepository<UserSpace, Long> {
    boolean existsByUser_IdAndSpace_Id(long userId, long spaceId);
    boolean existsByUser_IdAndSpace_IdAndIsSpaceAdminTrue(long userId, long spaceId); // Metodo per verificare se è admin dello spazio

    List<UserSpace> findAllBySpace(Space space);

    List<UserSpace> findByUser(User user);

    UserSpace findFirstBySpaceAndIsSpaceAdmin(Space space, boolean isSpaceAdmin);

    @Transactional
    @Modifying
    int deleteByUser_IdAndSpace_Id(long userId, long spaceId);
}