package com.astroverse.backend.controller;

import com.astroverse.backend.component.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean isValid = jwtUtil.isTokenValid(token);
        if(isValid) {
            return ResponseEntity.ok("Token valido");   //200 token valido
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token non valido"); //401 non autorizzato all'accesso al token
        }
    }
}