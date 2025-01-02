package com.astroverse.backend.service;

import com.astroverse.backend.model.Space;
import com.astroverse.backend.repository.SpaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        long spaceId = 123;
        String image = "newImage.png";
        when(spaceRepository.updateImageById(spaceId, image)).thenReturn(1);

        int updatedRows = spaceRepository.updateImageById(spaceId, image);

        assertEquals(1, updatedRows);
        verify(spaceRepository, times(1)).updateImageById(spaceId, image);
    }

    @Test
    public void testUpdateSpace() {
        space = new Space("Scienze", "Fisica Terrestre", "Spazi dedito alla divulgazione scientifica");
        when(spaceRepository.updateSpaceDetailsById(space.getId(), space.getTitle(), space.getArgument(), space.getDescription())).thenReturn(1);

        int updatedRows = spaceRepository.updateSpaceDetailsById(space.getId(), space.getTitle(), space.getArgument(), space.getDescription());

        assertEquals(1, updatedRows);
        verify(spaceRepository, times(1)).updateSpaceDetailsById(space.getId(), space.getTitle(), space.getArgument(), space.getDescription());
    }
}