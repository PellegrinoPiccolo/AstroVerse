package com.astroverse.backend.controller;

import com.astroverse.backend.component.ChangeUserRequest;
import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.component.Hash;
import com.astroverse.backend.model.User;
import com.astroverse.backend.repository.UserRepository;
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
        mockUser.setPassword("");

        mockUser.setEmail("test2@gmail.com");

        ChangeUserRequest changeUserRequest = new ChangeUserRequest(mockUser, "");

        when(userService.changeUserData(mockUser.getId(), mockUser.getEmail(), mockUser.getUsername(), mockUser.getNome(), mockUser.getCognome())).thenReturn(mockUser);
        ResponseEntity<?> responseChangeData = authController.changeUserData(changeUserRequest);

        assertEquals(HttpStatus.OK, responseChangeData.getStatusCode());

        mockUser.setPassword("Prova!222");
        changeUserRequest.setConfermaPassword("Prova!222");

        when(userService.changeUserData(mockUser.getId(), mockUser.getEmail(), mockUser.getUsername(), mockUser.getNome(), mockUser.getCognome())).thenReturn(mockUser);
        ResponseEntity<?> responseChangePassword = authController.changeUserData(changeUserRequest);

        assertEquals(HttpStatus.OK, responseChangePassword.getStatusCode());
    }
}