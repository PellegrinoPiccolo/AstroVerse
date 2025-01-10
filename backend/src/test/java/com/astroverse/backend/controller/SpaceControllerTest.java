package com.astroverse.backend.controller;

import com.astroverse.backend.model.Space;
import com.astroverse.backend.model.User;
import com.astroverse.backend.model.UserSpace;
import com.astroverse.backend.repository.SpaceRepository;
import com.astroverse.backend.repository.UserRepository;
import com.astroverse.backend.service.SpaceService;
import com.astroverse.backend.service.UserService;
import com.astroverse.backend.service.UserSpaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpaceControllerTest {
    @Mock
    private SpaceService spaceService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserSpaceService userSpaceService;
    @InjectMocks
    private SpaceController spaceController;
    @Mock
    private SpaceRepository spaceRepository;

    private final String exampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIERhdGEiLCJpZCI6MSwiZW1haWwiOiJwcm92YXRlc3RAZ21haWwuY29tIiwidXNlcm5hbWUiOiJwcm92YVVzZXJuYW1lIiwibm9tZSI6InByb3ZhIiwiY29nbm9tZSI6InByb3ZhIiwiaXNBZG1pbiI6ZmFsc2UsImlhdCI6MTczNTkwMDQ0MywiaXNzIjoiQXN0cm92ZXJzZSIsImV4cCI6MTczODQ5MjQ0M30.KOOIxhlzWj-UdhbcscVB1CXoNAd6lsxfOFKRt3diYvk";

    @Test
    public void testCreateSpace() {
        Space space = new Space("Titolo", "Argomento", "Descrizione");

        User user = new User("prova", "prova", "provaUsername", "provatest@gmail.com", "provaPassword!22");

        UserSpace userSpace = new UserSpace(user, space);

        when(spaceService.saveSpace(any(Space.class))).thenReturn(space);
        when(userService.getUser(user.getEmail())).thenReturn(user);
        when(userSpaceService.saveUserSpaceAdmin(any(UserSpace.class))).thenReturn(userSpace);
        ResponseEntity<?> response = spaceController.createSpace(space.getTitle(), space.getArgument(), space.getDescription(), null, exampleToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testModifySpace() {
        Space space = new Space("Titolo", "Argomento", "Descrizione");

        User user = new User("prova", "prova", "provaUsername", "provatest@gmail.com", "provaPassword!22");
        UserSpace userSpace = new UserSpace(user, space);

        when(spaceRepository.save(space)).thenReturn(space);
        space = spaceRepository.save(space);
        space.setTitle("TitoloModificato");

        when(spaceService.getSpace(space.getId())).thenReturn(Optional.of(space));
        when(userService.getUser(user.getEmail())).thenReturn(user);
        when(userSpaceService.existSubscribe(any(UserSpace.class))).thenReturn(true);
        when(userSpaceService.isUserAdmin(any(UserSpace.class))).thenReturn(true);
        when(spaceService.updateSpace(space.getId(), space.getTitle(), space.getDescription(), space.getArgument())).thenReturn(1);
        ResponseEntity<?> response = spaceController.modifySpace(space.getId(), exampleToken, space.getTitle(), space.getArgument(), space.getDescription(), null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}