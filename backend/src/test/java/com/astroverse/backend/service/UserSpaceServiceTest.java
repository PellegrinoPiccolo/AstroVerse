package com.astroverse.backend.service;

import com.astroverse.backend.model.Space;
import com.astroverse.backend.model.User;
import com.astroverse.backend.model.UserSpace;
import com.astroverse.backend.repository.SpaceRepository;
import com.astroverse.backend.repository.UserRepository;
import com.astroverse.backend.repository.UserSpaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserSpaceServiceTest {
    @Mock
    private UserSpaceRepository userSpaceRepository;
    @InjectMocks
    private UserSpaceService userSpaceService;
    @InjectMocks
    private UserSpace userSpace;
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SpaceRepository spaceRepository;
    @InjectMocks
    private SpaceService spaceService;

    @Test
    public void testSaveUserSpace() {
        User user = new User();
        Space space = new Space();
        userSpace = new UserSpace(user, space);
        when(userSpaceRepository.save(userSpace)).thenReturn(userSpace);

        UserSpace savedUserSpace = userSpaceService.saveUserSpace(userSpace);

        assertNotNull(savedUserSpace);
        assertEquals(space, savedUserSpace.getSpace());
        assertEquals(user, savedUserSpace.getUser());
        verify(userSpaceRepository, times(1)).save(userSpace);
    }

    @Test
    public void testSaveUserSpaceAdmin() {
        User user = new User("prova", "prova", "provaUsername", "provatest@gmail.com", "provaPassword!22");
        user.setAdmin(true);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        user = userService.saveUser(user);
        Space space = new Space("TitoloSpazio", "ArgomentoSpazio", "DescrizioneSpazio");
        when(spaceRepository.save(space)).thenReturn(space);
        space = spaceService.saveSpace(space);
        UserSpace userSpace = new UserSpace(user, space);

        when(userSpaceRepository.existsByUser_IdAndSpace_IdAndIsSpaceAdminTrue(user.getId(), space.getId())).thenReturn(true);
        boolean isAdmin = userSpaceService.isUserAdmin(userSpace);
        assertTrue(isAdmin);
        verify(userSpaceRepository, times(1)).existsByUser_IdAndSpace_IdAndIsSpaceAdminTrue(user.getId(), space.getId());

        when(userSpaceRepository.save(userSpace)).thenReturn(userSpace);

        UserSpace savedUserSpace = userSpaceService.saveUserSpaceAdmin(userSpace);

        assertNotNull(savedUserSpace);
        assertTrue(savedUserSpace.isSpaceAdmin());
        verify(userSpaceRepository, times(1)).save(userSpace);
    }
}