package com.astroverse.backend.service;

import com.astroverse.backend.model.Space;
import com.astroverse.backend.repository.SpaceRepository;
import org.springframework.stereotype.Service;

@Service
public class SpaceService {
    private final SpaceRepository spaceRepository;

    public SpaceService(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    public Space saveSpace(Space space) {
        return spaceRepository.save(space);
    }
}