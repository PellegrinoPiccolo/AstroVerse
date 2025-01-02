package com.astroverse.backend.controller;

import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.component.Hash;
import com.astroverse.backend.model.User;
import com.astroverse.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthController authController;

    @Test
    public void testRegistration() {
        String nome = "Test";
        String cognome = "User";
        String email = "test@example.com";
        String username = "testuser";
        String password = "Password!22";

        String hashedPassword = Hash.hashPassword(password);

        User mockUser = new User(nome, cognome, username, email, hashedPassword);
        mockUser.setId(1L);

        when(userService.saveUser(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken(
                mockUser.getId(),
                mockUser.getEmail(),
                mockUser.getUsername(),
                mockUser.getNome(),
                mockUser.getCognome(),
                mockUser.isAdmin()
        )).thenReturn("mockedAccessToken");

        ResponseEntity<?> response = authController.registrationUser(nome, cognome, email, username, password);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("mockedAccessToken", responseBody.get("accessToken"));

        verify(userService, times(1)).saveUser(mockUser);
        verify(jwtUtil, times(1)).generateToken(
                mockUser.getId(),
                mockUser.getEmail(),
                mockUser.getUsername(),
                mockUser.getNome(),
                mockUser.getCognome(),
                mockUser.isAdmin()
        );
    }

    @Test
    public void testLogin() {
        String email = "test@example.com";
        String password = "Password!22";

        String hashedPassword = Hash.hashPassword(password);
        User mockUser = new User("Test", "User", "testuser", email, hashedPassword);

        mockUser.setId(50);

        String mockedAccessToken = "mockedAccessToken";

        when(userService.getUser(email)).thenReturn(mockUser);
        when(jwtUtil.generateToken(
                mockUser.getId(),
                mockUser.getEmail(),
                mockUser.getUsername(),
                mockUser.getNome(),
                mockUser.getCognome(),
                mockUser.isAdmin()
        )).thenReturn(mockedAccessToken);

        ResponseEntity<?> response = authController.loginUser(email, password);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(mockedAccessToken, responseBody.get("accessToken"));

        verify(userService, times(1)).getUser(email);
        verify(jwtUtil, times(1)).generateToken(
                mockUser.getId(),
                mockUser.getEmail(),
                mockUser.getUsername(),
                mockUser.getNome(),
                mockUser.getCognome(),
                mockUser.isAdmin()
        );
    }
}