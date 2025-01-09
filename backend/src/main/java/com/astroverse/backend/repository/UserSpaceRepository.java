package com.astroverse.backend.repository;

import com.astroverse.backend.model.Space;
import com.astroverse.backend.model.User;
import com.astroverse.backend.model.UserSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserSpaceRepository extends JpaRepository<UserSpace, Long> {
    boolean existsByUser_IdAndSpace_Id(Long userId, Long spaceId);
    boolean existsByUser_IdAndSpace_IdAndIsSpaceAdminTrue(Long userId, Long spaceId); // Metodo per verificare se è admin

    List<UserSpace> findAllBySpace(Space space);

    List<UserSpace> findByUser(User user);

    UserSpace findFirstBySpaceAndIsSpaceAdmin(Space space, boolean isSpaceAdmin);
}