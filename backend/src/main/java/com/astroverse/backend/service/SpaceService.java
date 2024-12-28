package com.astroverse.backend.service;

import com.astroverse.backend.model.Space;
import com.astroverse.backend.repository.SpaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpaceService {
    private final SpaceRepository spaceRepository;

    public SpaceService(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    public Space saveSpace(Space space) {
        return spaceRepository.save(space);
    }

    public int saveImage(long id, String image) {
        return spaceRepository.updateImageById(id, image);
    }
    public Optional<Space> getSpace(long id) {
        return spaceRepository.getSpaceById(id);
    }
    public int updateImage(long id, String image) {
        return spaceRepository.updateImageById(id, image);
    }
    public int updateSpace(long id, String titolo, String descrizione, String argomento) {
        return spaceRepository.updateSpaceDetailsById(id, titolo, descrizione, argomento);
    }

    public List<Space> searchSpace(String search) {
        return spaceRepository.findByTitleContainingIgnoreCaseOrArgumentContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, search);
    }
}