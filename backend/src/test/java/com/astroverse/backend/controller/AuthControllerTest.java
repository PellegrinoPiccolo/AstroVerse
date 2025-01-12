package com.astroverse.backend.controller;

import com.astroverse.backend.component.ChangeUserRequest;
import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.component.Hash;
import com.astroverse.backend.model.TokenBlackList;
import com.astroverse.backend.model.User;
import com.astroverse.backend.repository.UserRepository;
import com.astroverse.backend.service.TokenBlackListService;
import com.astroverse.backend.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthController authController;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private TokenBlackList tokenBlackList;
    @Mock
    private TokenBlackListService tokenBlackListService;

    @Test
    public void testRegistration() {
        User mockUser = new User("Test", "User", "testUser", "test@example.com", "Password!22");

        when(userService.saveUser(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken(mockUser.getId(), mockUser.getEmail(), mockUser.getUsername(), mockUser.getNome(), mockUser.getCognome(), mockUser.isAdmin())).thenReturn("accessToken");
        ResponseEntity<?> response = authController.registrationUser(
                mockUser.getNome(),
                mockUser.getCognome(),
                mockUser.getEmail(),
                mockUser.getUsername(),
                mockUser.getPassword()
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> responseBody = objectMapper.convertValue(response.getBody(), new TypeReference<>() {});
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("accessToken"));

        verify(userService, times(1)).saveUser(any(User.class));

        when(userRepository.getUserById(mockUser.getId())).thenReturn(mockUser);
        mockUser = userRepository.getUserById(mockUser.getId());
        assertNotNull(mockUser);
        assertEquals(mockUser.getUsername(), "testUser");
        verify(userRepository, times(1)).getUserById(mockUser.getId());
    }

    @Test
    public void testLogin() {
        User mockUser = new User("Test", "User", "testUser", "test@example.com", "Password!22");

        when(userService.saveUser(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken(mockUser.getId(), mockUser.getEmail(), mockUser.getUsername(), mockUser.getNome(), mockUser.getCognome(), mockUser.isAdmin())).thenReturn("accessToken");
        ResponseEntity<?> response = authController.registrationUser(
                mockUser.getNome(),
                mockUser.getCognome(),
                mockUser.getEmail(),
                mockUser.getUsername(),
                mockUser.getPassword()
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());

        String hashPassword = Hash.hashPassword(mockUser.getPassword());
        mockUser.setPassword(hashPassword);

        when(userService.getUser("test@example.com")).thenReturn(mockUser);
        when(jwtUtil.generateToken(mockUser.getId(), mockUser.getEmail(), mockUser.getUsername(), mockUser.getNome(), mockUser.getCognome(), mockUser.isAdmin())).thenReturn("accessToken");
        ResponseEntity<?> responseLogin = authController.loginUser(mockUser.getEmail(), "Password!22");

        assertEquals(HttpStatus.OK, responseLogin.getStatusCode());
    }

    @Test
    public void testModifyUserData() {
        User mockUser = new User("Test", "User", "testUser", "test@example.com", "Password!22");
        mockUser.setId(1L);
        when(jwtUtil.generateToken(mockUser.getId(), mockUser.getEmail(), mockUser.getUsername(), mockUser.getNome(), mockUser.getCognome(), mockUser.isAdmin())).thenReturn("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIERhdGEiLCJpZCI6MSwiZW1haWwiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcm5hbWUiOiJ0ZXN0VXNlciIsIm5vbWUiOiJUZXN0IiwiY29nbm9tZSI6IlVzZXIiLCJpc0FkbWluIjpmYWxzZSwiaWF0IjoxNzM2MjYyNTcxLCJpc3MiOiJBc3Ryb3ZlcnNlIiwiZXhwIjoxNzM4ODU0NTcxfQ.y8oJfzebNQ2DbHG0LRcXopvcgTTgfmQUuJ9v9eC7Frc");
        String token = jwtUtil.generateToken(mockUser.getId(), mockUser.getEmail(), mockUser.getUsername(), mockUser.getNome(), mockUser.getCognome(), mockUser.isAdmin());

        mockUser.setEmail("test2@example.com");
        mockUser.setPassword("");
        when(userService.changeUserData(mockUser.getId(), mockUser.getEmail(), mockUser.getUsername(), mockUser.getNome(), mockUser.getCognome(), "test@example.com", mockUser.getUsername())).thenReturn(mockUser);
        ChangeUserRequest changeUserRequest = new ChangeUserRequest(mockUser, "", "");
        ResponseEntity<?> response = authController.changeUserData(mockUser.getNome(), mockUser.getCognome(), mockUser.getEmail(), mockUser.getUsername(), mockUser.getPassword(), "", "", token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}