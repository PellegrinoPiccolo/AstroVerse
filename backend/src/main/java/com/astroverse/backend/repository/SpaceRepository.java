package com.astroverse.backend.repository;

import com.astroverse.backend.model.Space;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Space s SET s.image = :image WHERE s.id = :id")
    int updateImageById(@Param("id") long id,
                       @Param("image") String image);

    Optional<Space> getSpaceById(long id);
    @Transactional
    @Modifying
    @Query("UPDATE Space s SET s.title = :title, s.description = :description, s.argument = :argument WHERE s.id = :id")
    int updateSpaceDetailsById(Long id, String title, String description, String argument);
    List<Space> findByTitleContainingIgnoreCaseOrArgumentContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String searchTitle, String searchArgument, String searchDescription);
}