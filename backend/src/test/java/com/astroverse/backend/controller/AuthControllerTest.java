package com.astroverse.backend.controller;

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
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
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
        Map<String, String> responseBody = objectMapper.convertValue(response.getBody(), new TypeReference<>(){});
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
//        String email = "test@example.com";
//        String password = "Password!22";
//
//        String hashedPassword = Hash.hashPassword(password);
//        User mockUser = new User("Test", "User", "testuser", email, hashedPassword);
//
//        mockUser.setId(50);
//
//        String mockedAccessToken = "mockedAccessToken";
//
//        when(userService.getUser(email)).thenReturn(mockUser);
//        when(jwtUtil.generateToken(
//                mockUser.getId(),
//                mockUser.getEmail(),
//                mockUser.getUsername(),
//                mockUser.getNome(),
//                mockUser.getCognome(),
//                mockUser.isAdmin()
//        )).thenReturn(mockedAccessToken);
//
//        ResponseEntity<?> response = authController.loginUser(email, password);
//
//        assertEquals(200, response.getStatusCodeValue());
//        Map<String, String> responseBody = (Map<String, String>) response.getBody();
//        assertNotNull(responseBody);
//        assertEquals(mockedAccessToken, responseBody.get("accessToken"));
//
//        verify(userService, times(1)).getUser(email);
//        verify(jwtUtil, times(1)).generateToken(
//                mockUser.getId(),
//                mockUser.getEmail(),
//                mockUser.getUsername(),
//                mockUser.getNome(),
//                mockUser.getCognome(),
//                mockUser.isAdmin()
//        );
    }

    @Test
    public void testModifyUserData() {

    }
}