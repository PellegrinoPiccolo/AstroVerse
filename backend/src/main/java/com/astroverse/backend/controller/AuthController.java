package com.astroverse.backend.controller;

import com.astroverse.backend.component.ChangeUserRequest;
import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.component.Hash;
import com.astroverse.backend.component.UserDTO;
import com.astroverse.backend.model.Preference;
import com.astroverse.backend.model.TokenBlackList;
import com.astroverse.backend.model.User;
import com.astroverse.backend.service.PreferenceService;
import com.astroverse.backend.service.TokenBlackListService;
import com.astroverse.backend.service.UserService;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenBlackListService tokenBlackListService;
    private final PreferenceService preferenceService;
    private static final String namesRegex = "^[A-Za-zÀ-ÿ\\s]{2,30}$";
    private static final String usernameRegex = "^[A-Za-z0-9._\\-\\s]{3,20}$";
    private static final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";

    public AuthController(JwtUtil jwtUtil, UserService userService, TokenBlackListService tokenBlackListService, PreferenceService preferenceService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.tokenBlackListService = tokenBlackListService;
        this.preferenceService = preferenceService;
    }

    public boolean isValidText(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text).matches();
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();
        token = token.replace("Bearer ", "");
        boolean isValid = jwtUtil.isTokenValid(token);
        DecodedJWT decodedJWT = JwtUtil.JwtDecode(token);
        if (isValid) {
            try {
                userService.getUser(decodedJWT.getClaim("email").asString());
            } catch (IllegalArgumentException e) {
                response.put("message", "L'utente non esiste");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); //401 non autorizzato all'accesso al token
            }
            response.put("message", "Token valido");
            return ResponseEntity.ok(response);   //200 token valido
        } else {
            response.put("message", "Token non valido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); //401 non autorizzato all'accesso al token
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registrationUser(@RequestParam String nome, @RequestParam String cognome, @RequestParam String email, @RequestParam String username, @RequestParam String password, @RequestParam List<String> preference) {
        Map<String, String> response = new HashMap<>();
        User user = new User(nome, cognome, username, email, password);
        if (!isValidText(user.getNome(), namesRegex)) {
            response.put("error", "Nome non valido");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        if (!isValidText(user.getCognome(), namesRegex)) {
            response.put("error", "Cognome non valido");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        if (!isValidText(user.getUsername(), usernameRegex)) {
            response.put("error", "Username non valido");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(!isValidText(user.getEmail(), emailRegex)) {
            response.put("error", "Email non valida");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(!isValidText(user.getPassword(), passwordRegex)) {
            response.put("error", "Password non valida");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (preference.isEmpty()) {
            response.put("error", "Numero di preferenze non valido");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
            if (accessToken == null) {
                response.put("error", "Errore nella generazione del token");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            for (String p : preference) {
                Preference pr = new Preference(userWithId, p);
                preferenceService.savePreference(pr);
            }
            response.put("accessToken", accessToken);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", "Utente già esistente");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email, @RequestParam String password) {
        Map<String, String> response = new HashMap<>();
        try {
            User user = userService.getUser(email);
            String userPassword = user.getPassword();
            if(!Hash.checkPassword(password, userPassword)) {
                response.put("error", "Password errata");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            String accessToken = jwtUtil.generateToken(
                    user.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getNome(),
                    user.getCognome(),
                    user.isAdmin()
            );
            response.put("accessToken", accessToken);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", "L'utente non esiste");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/change-user-data")
    public ResponseEntity<?> changeUserData(@RequestParam String nome, @RequestParam String cognome, @RequestParam String email, @RequestParam String username, @RequestParam String psw, @RequestParam String confirmPassword, @RequestParam String vecchiaPassword, @RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();
        User user = new User(nome, cognome, username, email, psw);
        ChangeUserRequest request = new ChangeUserRequest(user, confirmPassword, vecchiaPassword);
        try {
            token = token.replace("Bearer ", "");
            String confermaPassword = request.getConfermaPassword();
            String password = user.getPassword();
            DecodedJWT decodedJWT = JwtUtil.JwtDecode(token);
            long id = decodedJWT.getClaim("id").asLong();
            String oldEmail = decodedJWT.getClaim("email").asString();
            String oldUsername = decodedJWT.getClaim("username").asString();
            if (user.getEmail().isEmpty() || user.getUsername().isEmpty() || user.getNome().isEmpty() || user.getCognome().isEmpty()) {
                response.put("error", "Uno o più dati dell'utente mancanti");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else if(!isValidText(user.getEmail(), emailRegex)) {
                response.put("error", "Email non valida");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else if (!isValidText(user.getNome(), namesRegex)) {
                response.put("error", "Nome non valido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else if(!isValidText(user.getCognome(), namesRegex)) {
                response.put("error", "Cognome non valido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else if (!isValidText(user.getUsername(), usernameRegex)) {
                response.put("error", "Username non valido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                user = userService.changeUserData(id, user.getEmail(), user.getUsername(), user.getNome(), user.getCognome(), oldEmail, oldUsername);
            }
            if(!password.isEmpty()) {
                String oldPassword = userService.getOldPassword(id);
                if(!isValidText(password, passwordRegex)) {
                    response.put("error", "Password non valida");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                } else if(!password.equals(confermaPassword)) {
                    response.put("error", "La password di conferma non è diversa dalla nuova pasword");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                } else if (!Hash.checkPassword(request.getVecchiaPassword(), oldPassword)) {
                    response.put("error", "La password corrente non è corretta");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                } else {
                    String hashPassword = Hash.hashPassword(password);
                    user.setPassword(hashPassword);
                    userService.changePassword(user.getUsername(), hashPassword);
                }
            }
            String newToken = jwtUtil.generateToken(id, user.getEmail(), user.getUsername(), user.getNome(), user.getCognome(), user.isAdmin());
            TokenBlackList tokenBlackList = new TokenBlackList();
            tokenBlackList.setAccessToken(token);
            tokenBlackListService.saveAccessToken(tokenBlackList);
            response.put("message", "Dati aggiornati");
            response.put("accessToken", newToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();
        try {
            token = token.replace("Bearer ", "");
            if (!jwtUtil.isTokenValid(token)) {
                response.put("error", "Token non valido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            TokenBlackList tokenBlackList = new TokenBlackList();
            tokenBlackList.setAccessToken(token);
            tokenBlackListService.saveAccessToken(tokenBlackList);
            response.put("message", "Logout effettuato con successo");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", "L'utente non esiste");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/view-account")
    public ResponseEntity<?> viewAccount(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            token = token.replace("Bearer ", "");
            if(!jwtUtil.isTokenValid(token)) {
                response.put("error", "Token non valido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            DecodedJWT decodedJWT = JwtUtil.JwtDecode(token);
            Long idString = decodedJWT.getClaim("id").asLong();
            if (idString == null) {
                response.put("error", "Id utente non presente");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            User user = userService.getUserData(idString);
            UserDTO userDTO = new UserDTO(user.getUserSpaces(), user.getPosts(), user.getNome(), user.getCognome(), user.getUsername(), user.getEmail());
            response.put("user", userDTO);
            response.put("spaces", userService.getSpaceByUser(user));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", "L'utente non esiste");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}