package com.astroverse.backend.controller;

import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.model.Post;
import com.astroverse.backend.model.User;
import com.astroverse.backend.service.PostService;
import com.astroverse.backend.service.UserService;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("api/post")
public class PostController {
    private static final String testoRegex = "^[\\w\\s\\p{Punct}]{1,400}$";
    private final UserService userService;
    private final PostService postService;
    private static final String directory = "uploads-post/";

    public PostController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<?> createPost(@RequestParam String testo, @RequestParam(value = "file", required = false) MultipartFile file, @RequestHeader("Authorization") String token, @PathVariable long id) {
        token = token.replace("Bearer ", "");
        if(!isValidText(testo, testoRegex)) {
            return ResponseEntity.status(400).body("Formato del testo non valido");
        }
        DecodedJWT decoded = JwtUtil.JwtDecode(token);
        String email = decoded.getClaim("email").asString();
        User user = userService.getUser(email);
        Post post = new Post(testo, id, user.getId());
        Post createdPost = postService.savePost(post);
        if (file != null && !file.isEmpty()) {
            if(!checkImageFile(file)) {
                return ResponseEntity.status(400).body("Formato dell'immagine non valido");
            }
            Path path = Paths.get(directory);
            Path postPath = Paths.get(directory + "/" + createdPost.getId() + "/");
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!Files.exists(postPath)) {
                try {
                    Files.createDirectories(postPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path filePath = postPath.resolve(fileName);
            try {
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            post.setFile(filePath.toString());
            if(postService.saveImage(post.getId(), post.getFile()) == 0) {
                return ResponseEntity.status(500).body("Errore nel salvataggio dell'immagine");
            }
        }
        return ResponseEntity.ok("Nuovo post creato con successo");
    }

    protected boolean checkImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.equals("image/jpeg") || contentType.equals("image/png");
    }

    public boolean isValidText(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text).matches();
    }
}