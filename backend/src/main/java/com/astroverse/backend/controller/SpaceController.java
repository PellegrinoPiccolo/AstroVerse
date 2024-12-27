package com.astroverse.backend.controller;

import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.model.Space;
import com.astroverse.backend.model.User;
import com.astroverse.backend.model.UserSpace;
import com.astroverse.backend.service.SpaceService;
import com.astroverse.backend.service.UserService;
import com.astroverse.backend.service.UserSpaceService;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("api/space")
public class SpaceController {
    private final SpaceService spaceService;
    private static final String titoloRegex = "^[A-Za-zÀ-ù0-9,‘\\-\\s]{2,50}$";
    private static final String argomentoRegex = "^[A-Za-zÀ-ÿ\\s]{2,30}$";
    private static final String descrizioneRegex = "^[\\w\\s\\p{Punct}]{1,200}$";
    private static final String directory = "uploads/";
    private final UserService userService;
    private final UserSpaceService userSpaceService;

    public SpaceController(SpaceService spaceService, UserService userService, UserSpaceService userSpaceService) {
        this.spaceService = spaceService;
        this.userService = userService;
        this.userSpaceService = userSpaceService;
    }

    public boolean isValidText(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text).matches();
    }

    @PostMapping("/create/")
    public ResponseEntity<?> createSpace(@RequestParam String titolo, @RequestParam String argomento, @RequestParam String descrizione, @RequestParam(value = "file", required = false) MultipartFile file) {
        if (!isValidText(titolo, titoloRegex) && titolo.isEmpty()) {
            return ResponseEntity.status(400).body("Errore nel formato del titolo");
        } else if (!isValidText(argomento, argomentoRegex) && argomento.isEmpty()) {
            return ResponseEntity.status(400).body("Errore nel formato dell'argomento");
        } else if (!isValidText(descrizione, descrizioneRegex) && descrizione.isEmpty()) {
            return ResponseEntity.status(400).body("Errore nel formato della descrizione");
        }
        Space space = new Space(titolo, argomento, descrizione);
        Space createdSpace = spaceService.saveSpace(space);
        if (createdSpace != null) {
            if (file != null && !file.isEmpty()) {
                String contentType = file.getContentType();
                if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
                    return ResponseEntity.status(400).body("Formato immagine non corretto");
                }
                Path path = Paths.get(directory);
                Path spacePath = Paths.get(directory + "/" + createdSpace.getId() + "/");
                if(!Files.exists(path)) {
                    try {
                        Files.createDirectories(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(!Files.exists(spacePath)) {
                    try {
                        Files.createDirectories(spacePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
                Path filePath = spacePath.resolve(fileName);
                try {
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                createdSpace.setImage(spacePath + fileName);
                if (spaceService.saveImage(createdSpace.getId(), createdSpace.getImage()) == 0) {
                    return ResponseEntity.status(500).body("Errore nel caricamento dell'immagine");
                }
            }
            return ResponseEntity.ok("Spazio Creato");
        }
        return ResponseEntity.status(500).body("Errore nella creazione dell spazio");
    }

    @GetMapping("/view/{id}/")
    public ResponseEntity<?> viewSpace(@PathVariable long id) {
        Optional<Space> optional = spaceService.getSpace(id);
        if (optional.isPresent()) {
            return ResponseEntity.ok(optional.get());
        } else {
            return ResponseEntity.status(400).body("Questo spazio non esiste");
        }
    }

    @PostMapping("/subscribe/")
    public ResponseEntity<?> subscribeSpace(@RequestParam Long idSpazio, @RequestHeader("Authorization") String token) {
        if (idSpazio == null) {
            return ResponseEntity.status(500).body("Errore nell'iscrizione allo spazio desiderato");
        }
        token = token.replace("Bearer ", "");
        DecodedJWT decodedJWT = JwtUtil.JwtDecode(token);
        String email = decodedJWT.getClaim("email").asString();
        User user = userService.getUser(email);
        Optional<Space> optional = spaceService.getSpace(idSpazio);
        if (optional.isEmpty()) {
            return ResponseEntity.status(400).body("Lo spazio non esiste");
        }
        Space space = optional.get();
        UserSpace userSpace = new UserSpace(user, space);
        if(!userSpaceService.existSubscribe(userSpace)) {
            userSpaceService.saveUserSpace(userSpace);
            return ResponseEntity.ok("Iscrizione allo spazio avvenuta con successo");
        } else  {
            return ResponseEntity.status(400).body("Iscrizione allo spazio già effettuata");
        }
    }
}