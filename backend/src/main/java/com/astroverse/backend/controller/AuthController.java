package com.astroverse.backend.controller;

import com.astroverse.backend.component.ChangeUserRequest;
import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.component.Hash;
import com.astroverse.backend.component.UserDTO;
import com.astroverse.backend.model.TokenBlackList;
import com.astroverse.backend.model.User;
import com.astroverse.backend.service.TokenBlackListService;
import com.astroverse.backend.service.UserService;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenBlackListService tokenBlackListService;
    private static final String namesRegex = "^[A-Za-zÀ-ÿ\\s]{2,30}$";
    private static final String usernameRegex = "^[A-Za-z0-9._\\-\\s]{3,20}$";
    private static final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";

    public AuthController(JwtUtil jwtUtil, UserService userService, TokenBlackListService tokenBlackListService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.tokenBlackListService = tokenBlackListService;
    }
    public boolean isValidText(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text).matches();
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
        if (!isValidText(user.getNome(), namesRegex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome non valido");
        }
        if (!isValidText(user.getCognome(), namesRegex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cognome non valido");
        }
        if (!isValidText(user.getUsername(), usernameRegex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username non valido");
        }
        if(!isValidText(user.getEmail(), emailRegex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email non valida");
        }
        if(!isValidText(user.getPassword(), passwordRegex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password non valida");
        }
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("L'utente non esiste");
        }
    }

    @PostMapping("/change-user-data")
    public ResponseEntity<?> changeUserData(@RequestHeader("Authorization") String token, @RequestBody ChangeUserRequest request) {
        try {
            User user = request.getUser();
            String confermaPassword = request.getConfermaPassword();
            String password = user.getPassword();
            token = token.replace("Bearer ", "");
            if (!jwtUtil.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token non valido");
            }
            if (user.getEmail().isEmpty() || user.getUsername().isEmpty() || user.getNome().isEmpty() || user.getCognome().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Uno o più dati dell'utente mancanti");
            } else if(!isValidText(user.getEmail(), emailRegex)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email non valida");
            } else if (!isValidText(user.getNome(), namesRegex)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome non valido");
            } else if(!isValidText(user.getCognome(), namesRegex)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cognome non valido");
            } else if (!isValidText(user.getUsername(), usernameRegex)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username non valido");
            } else {
                user = userService.changeUserData(user.getId(), user.getEmail(), user.getUsername(), user.getNome(), user.getCognome());
            }
            if(!password.isEmpty()) {
                if(!isValidText(password, passwordRegex)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password non valida");
                } else if(!password.equals(confermaPassword)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La password di conferma non è uguale alla nuova password");
                } else {
                    String hashPassword = Hash.hashPassword(password);
                    user.setPassword(hashPassword);
                    userService.changePassword(user.getUsername(), hashPassword);
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body("Dati aggiornati");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            token = token.replace("Bearer ", "");
            if (!jwtUtil.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token non valido");
            }
            TokenBlackList tokenBlackList = new TokenBlackList();
            tokenBlackList.setAccessToken(token);
            tokenBlackListService.saveAccessToken(tokenBlackList);
            return ResponseEntity.status(HttpStatus.OK).body("Logout effettuato con successo");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("L'utente non esiste");
        }
    }

    @GetMapping("/view-account")
    public ResponseEntity<?> viewAccount(@RequestHeader("Authorization") String token) {
        try {
            token = token.replace("Bearer ", "");
            if(!jwtUtil.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token non valido");
            }
            DecodedJWT decodedJWT = JwtUtil.JwtDecode(token);
            Long idString = decodedJWT.getClaim("id").asLong();
            if (idString == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id utente non presente");
            }
            User user = userService.getUserData(idString);
            UserDTO userDTO = new UserDTO(user.getUserSpaces(), user.getUserPosts(), user.getNome(), user.getCognome(), user.getUsername(), user.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(userDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("L'utente non esiste");
        }
    }
}