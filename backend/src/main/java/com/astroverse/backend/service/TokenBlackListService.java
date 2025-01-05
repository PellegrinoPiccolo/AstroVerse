package com.astroverse.backend.service;

import com.astroverse.backend.model.TokenBlackList;
import com.astroverse.backend.repository.TokenBlackListRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenBlackListService {
    private final TokenBlackListRepository tokenBlackListRepository;

    public TokenBlackListService(TokenBlackListRepository tokenBlackListRepository) {
        this.tokenBlackListRepository = tokenBlackListRepository;
    }

    public void saveAccessToken(TokenBlackList token) {
        tokenBlackListRepository.save(token);
    }

    public boolean existToken(String token) {
        return tokenBlackListRepository.existsByAccessToken(token);
    }
}