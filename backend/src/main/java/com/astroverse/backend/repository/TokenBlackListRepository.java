package com.astroverse.backend.repository;

import com.astroverse.backend.model.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, Long> {
    boolean existsByAccessToken(String token);
}