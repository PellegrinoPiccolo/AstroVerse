package com.astroverse.backend.service;

import com.astroverse.backend.model.User;
import com.astroverse.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    @InjectMocks
    private User user;

    @Test
    public void testSaveUser() {
        user = new User("prova", "prova", "provaUsername", "provatest@gmail.com", "provaPassword!22");
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertNotNull(savedUser);
        assertEquals("provaUsername", savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testChangeUserData() {
        user = new User("prova", "prova", "provaUsername", "provatest@gmail.com", "provaPassword!22");
        when(userRepository.save(user)).thenReturn(user);
        user = userService.saveUser(user);
        when(userRepository.existsByEmailAndIdNot("orny05@gmail.com", user.getId())).thenReturn(false);
        when(userRepository.existsByUsernameAndIdNot("Orni05", user.getId())).thenReturn(false);
        when(userRepository.updateUserById(user.getId(), "Ornella", "Zarrella", "orny05@gmail.com", "Orni05")).thenReturn(1);
        user = userService.changeUserData(user.getId(), "orny05@gmail.com", "Orni05", "Ornella", "Zarrella");
        verify(userRepository, times(1)).updateUserById(
                user.getId(),
                "Ornella",
                "Zarrella",
                "orny05@gmail.com",
                "Orni05"
        );
        assertNotNull(user);
        assertEquals("Orni05", user.getUsername());
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);
        when(userRepository.updatePasswordByUsername(user.getUsername(), "Provaaaaa!22")).thenReturn(1);
        userService.changePassword(user.getUsername(), "Provaaaaa!22");
        user.setPassword("Provaaaaa!22");
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(userRepository.getUserById(user.getId())).thenReturn(user);
        user = userService.getUserData(user.getId());
        verify(userRepository, times(1)).getUserById(user.getId());
        assertEquals("Provaaaaa!22", user.getPassword());
        verify(userRepository, times(1)).updatePasswordByUsername(user.getUsername(), "Provaaaaa!22");
    }
}