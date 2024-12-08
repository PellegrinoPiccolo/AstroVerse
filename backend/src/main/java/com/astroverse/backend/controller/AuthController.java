package com.astroverse.backend.controller;

import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.component.Hash;
import com.astroverse.backend.model.User;
import com.astroverse.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody String token) {
        boolean isValid = jwtUtil.isTokenValid(token);
        if (isValid) {
            return ResponseEntity.ok("Token valido");   //200 token valido
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token non valido"); //401 non autorizzato all'accesso al token
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registrationUser(@RequestBody User user) {
        String hashPassword = Hash.hashPassword(user.getPassword());
        user.setPassword(hashPassword);
        try {
            User userWithId = userService.saveUser(user);
            String accessToken = jwtUtil.generateToken(
                    userWithId.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getNome(),
                    user.getCognome(),
                    user.isAdmin());
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Utente già esistente");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email, @RequestParam String password) {
        try {
            User user = userService.getUser(email);
            String userPassword = user.getPassword();
            if(!Hash.checkPassword(password, userPassword)) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password errata");
            }
            String accessToken = jwtUtil.generateToken(
                    user.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getNome(),
                    user.getCognome(),
                    user.isAdmin()
            );
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Utente non esiste");
        }
    }
}