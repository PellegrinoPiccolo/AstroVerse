package com.astroverse.backend.controller;

import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.model.Post;
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
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/space")
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

    @PostMapping("/create")
    public ResponseEntity<?> createSpace(@RequestParam String titolo, @RequestParam String argomento, @RequestParam String descrizione, @RequestParam(value = "file", required = false) MultipartFile file, @RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();
        if (!isValidText(titolo, titoloRegex) && titolo.isEmpty()) {
            response.put("message", "Errore nel formato del titolo");
            return ResponseEntity.status(400).body(response);
        } else if (!isValidText(argomento, argomentoRegex) && argomento.isEmpty()) {
            response.put("error", "Errore nel formato dell'argomento");
            return ResponseEntity.status(400).body(response);
        } else if (!isValidText(descrizione, descrizioneRegex) && descrizione.isEmpty()) {
            response.put("error", "Errore nel formato della descrizione");
            return ResponseEntity.status(400).body(response);
        }
        Space space = new Space(titolo, argomento, descrizione);
        Space createdSpace = spaceService.saveSpace(space);
        if (createdSpace != null) {
            if (file != null && !file.isEmpty()) {
                if(!checkImageFile(file)) {
                    response.put("error", "Formato immagine non valido");
                    return ResponseEntity.status(400).body(response);
                }
                Path path = Paths.get(directory);
                Path spacePath = Paths.get(directory + "\\" + createdSpace.getId() + "\\");
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
                if (!saveImageFile(createdSpace, spacePath, file)) {
                    response.put("error", "Errore nel caricamento dell'immagine");
                    return ResponseEntity.status(500).body(response);
                }
            }
            token = token.replace("Bearer ", "");
            DecodedJWT decoded = JwtUtil.JwtDecode(token);
            String email = decoded.getClaim("email").asString();
            UserSpace userSpace = new UserSpace(userService.getUser(email), createdSpace);
            userSpaceService.saveUserSpaceAdmin(userSpace);
            response.put("message", "Spazio Creato");
            return ResponseEntity.ok(response);
        }
        response.put("error", "Errore nella creazione dell spazio");
        return ResponseEntity.status(500).body(response);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewSpace(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Space> optional = spaceService.getSpace(id);
        if (optional.isPresent()) {
            Space space = optional.get();
            List<User> users = spaceService.getUsersBySpace(space);
            response.put("message", space);
            response.put("users", users);
            response.put("idAdmin", spaceService.getAdmin(space));
            List<Post> posts = spaceService.getPost(space);
            for (Post post : posts) {
                post.setUserData(new User(post.getUser().getId(),
                            post.getUser().getNome(),
                            post.getUser().getCognome(),
                            post.getUser().getUsername(),
                            post.getUser().getEmail())
                );
            }
            response.put("posts", spaceService.getPost(space));
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Questo spazio non esiste");
            return ResponseEntity.status(400).body(response);
        }
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeSpace(@RequestParam Long idSpazio, @RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();
        if (idSpazio == null) {
            response.put("error", "Errore nell'iscrizione allo spazio desiderato");
            return ResponseEntity.status(500).body(response);
        }
        token = token.replace("Bearer ", "");
        DecodedJWT decodedJWT = JwtUtil.JwtDecode(token);
        String email = decodedJWT.getClaim("email").asString();
        User user = userService.getUser(email);
        Optional<Space> optional = spaceService.getSpace(idSpazio);
        if (optional.isEmpty()) {
            response.put("error", "Lo spazio non esiste");
            return ResponseEntity.status(400).body(response);
        }
        Space space = optional.get();
        UserSpace userSpace = new UserSpace(user, space);
        if(!userSpaceService.existSubscribe(userSpace)) {
            userSpaceService.saveUserSpace(userSpace);
            response.put("message", "Iscrizione allo spazio avvenuta con successo");
            return ResponseEntity.ok(response);
        } else  {
            response.put("error", "Iscrizione allo spazio già effettuata");
            return ResponseEntity.status(400).body(response);
        }
    }

    @PostMapping("/modify/{id}")
    public ResponseEntity<?> modifySpace(@PathVariable Long id, @RequestHeader("Authorization") String token, @RequestParam String titolo, @RequestParam String argomento, @RequestParam String descrizione, @RequestParam(value = "file", required = false) MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        Optional<Space> optionalSpace = spaceService.getSpace(id);
        if (optionalSpace.isEmpty()) {
            response.put("error", "Questo spazio non esiste");
            return ResponseEntity.status(400).body(response);
        }
        Space space = optionalSpace.get();
        token = token.replace("Bearer ", "");
        DecodedJWT decodedJWT = JwtUtil.JwtDecode(token);
        String email = decodedJWT.getClaim("email").asString();
        User user = userService.getUser(email);
        UserSpace userSpace = new UserSpace(user, space);
        if (!userSpaceService.existSubscribe(userSpace)) {
            response.put("error", "L'utente non è iscritto allo spazio");
            return ResponseEntity.status(400).body(response);
        }
        if(!userSpaceService.isUserAdmin(userSpace)) {
            response.put("error", "L'utente non è admin");
            return ResponseEntity.status(400).body(response);
        }
        if (!isValidText(titolo, titoloRegex) && titolo.isEmpty()) {
            response.put("error", "Errore nel formato del titolo");
            return ResponseEntity.status(400).body(response);
        } else if (!isValidText(argomento, argomentoRegex) && argomento.isEmpty()) {
            response.put("error", "Errore nel formato dell'argomento");
            return ResponseEntity.status(400).body(response);
        } else if (!isValidText(descrizione, descrizioneRegex) && descrizione.isEmpty()) {
            response.put("error", "Errore nel formato della descrizione");
            return ResponseEntity.status(400).body(response);
        }
        if (file != null && !file.isEmpty()) {
            if(!checkImageFile(file)) {
                response.put("error", "Errore nel formato dell'immagine");
                return ResponseEntity.status(400).body(response);
            }
            Path path = Paths.get(directory);
            Path spacePath = Paths.get(directory + "" + space.getId() + "\\");
            if(!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            Path oldFilePath = Paths.get(space.getImage());
            try {
                if(Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!updateImageFile(space, spacePath, file)) {
                response.put("error", "Errore nel caricamento dell'immagine");
                return ResponseEntity.status(500).body(response);
            }
            response.put("message", "Modifica allo spazio avvenuta con successo");
            return ResponseEntity.ok(response);
        }
        space.setTitle(titolo);
        space.setArgument(argomento);
        space.setDescription(descrizione);
        if (spaceService.updateSpace(space.getId(), space.getTitle(), space.getDescription(), space.getArgument()) == 0) {
            response.put("error", "Errore nel salvataggio dello spazio");
            return ResponseEntity.status(500).body(response);
        }
        response.put("message", "Spazio modificato correttamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/{param}")
    public ResponseEntity<?> searchSpace(@PathVariable String param) {
        return ResponseEntity.ok(Map.of("message", spaceService.searchSpace(param)));
    }

    protected boolean checkImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.equals("image/jpeg") || contentType.equals("image/png");
    }

    protected boolean saveImageFile(Space space, Path spacePath, MultipartFile file) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename().replaceAll("\\s+", "");
        Path filePath = spacePath.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        space.setImage(filePath.toString());
        return spaceService.saveImage(space.getId(), space.getImage()) != 0;
    }

    protected boolean updateImageFile(Space space, Path spacePath, MultipartFile file) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path filePath = spacePath.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        space.setImage(filePath.toString());
        return spaceService.updateImage(space.getId(), space.getImage()) != 0;
    }
}