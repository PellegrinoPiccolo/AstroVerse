package com.astroverse.backend.service;

import com.astroverse.backend.model.Space;
import com.astroverse.backend.repository.SpaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpaceServiceTest {
    @Mock
    private SpaceRepository spaceRepository;
    @InjectMocks
    private SpaceService spaceService;
    @InjectMocks
    private Space space;

    @Test
    public void testSaveSpace() {
        space = new Space("Scienze#1", "Fisica terrestre", "Spazio dedito alla divulgazione scientifica");
        when(spaceRepository.save(space)).thenReturn(space);

        Space savedSpace = spaceService.saveSpace(space);

        assertNotNull(savedSpace);
        assertEquals("Scienze#1", savedSpace.getTitle());
        assertEquals("Fisica terrestre", savedSpace.getArgument());
        assertEquals("Spazio dedito alla divulgazione scientifica", savedSpace.getDescription());
        verify(spaceRepository, times(1)).save(space);
    }

    @Test
    public void testUpdateImageById() {
        Space space = new Space("TitoloSpazio", "ArgomentoSpazio", "DescrizioneSpazio");
        when(spaceRepository.save(space)).thenReturn(space);
        space = spaceService.saveSpace(space);
        String image = "newImage.png";
        when(spaceRepository.updateImageById(space.getId(), image)).thenReturn(1);

        int updatedRows = spaceService.updateImage(space.getId(), image);
        space.setImage(image);
        Optional<Space> optionalSpace = Optional.of(space);
        when(spaceRepository.getSpaceById(space.getId())).thenReturn(optionalSpace);
        optionalSpace = spaceService.getSpace(space.getId());
        assertTrue(optionalSpace.isPresent());
        space = optionalSpace.get();

        assertEquals(1, updatedRows);
        assertEquals(space.getImage(), "newImage.png");
        verify(spaceRepository, times(1)).updateImageById(space.getId(), image);
    }

    @Test
    public void testUpdateSpace() {
        space = new Space("Scienze", "Fisica Terrestre", "Spazi dedito alla divulgazione scientifica");
        when(spaceRepository.save(space)).thenReturn(space);
        space = spaceService.saveSpace(space);

        when(spaceRepository.updateSpaceDetailsById(space.getId(), "Fisica con Pelle", "Meccanica Quantistica", "Fisica")).thenReturn(1);
        int updatedRows = spaceService.updateSpace(space.getId(), "Fisica con Pelle", "Meccanica Quantistica", "Fisica");

        space.setTitle("Fisica con Pelle");
        space.setArgument("Fisica");
        space.setDescription("Meccanica Quantistica");

        Optional<Space> optionalSpace = Optional.of(space);
        when(spaceRepository.getSpaceById(space.getId())).thenReturn(optionalSpace);
        optionalSpace = spaceService.getSpace(space.getId());
        assertTrue(optionalSpace.isPresent());
        space = optionalSpace.get();

        assertEquals(1, updatedRows);
        assertEquals("Fisica con Pelle", space.getTitle());
        assertEquals("Fisica", space.getArgument());
        assertEquals("Meccanica Quantistica", space.getDescription());
        verify(spaceRepository, times(1)).updateSpaceDetailsById(space.getId(), space.getTitle(), space.getDescription(), space.getArgument());
    }
}