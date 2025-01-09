package com.astroverse.backend.controller;

import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.model.Post;
import com.astroverse.backend.model.User;
import com.astroverse.backend.service.PostService;
import com.astroverse.backend.service.VotePostFacade;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private static final String testoRegex = "^[\\w\\s\\p{Punct}]{1,400}$";
    private final PostService postService;
    private static final String directory = "uploads-post/";
    private final VotePostFacade votePostFacade;

    public PostController(PostService postService, VotePostFacade votePostFacade) {
        this.postService = postService;
        this.votePostFacade = votePostFacade;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<?> createPost(@RequestParam String testo, @RequestParam(value = "file", required = false) MultipartFile file, @RequestHeader("Authorization") String token, @PathVariable long id) {
        Map<String, String> response = new HashMap<>();
        token = token.replace("Bearer ", "");
        if(!isValidText(testo, testoRegex)) {
            response.put("error", "Formato del testo non valido");
            return ResponseEntity.status(400).body(response);
        }
        DecodedJWT decoded = JwtUtil.JwtDecode(token);
        User user = new User(decoded.getClaim("id").asLong());
        Post post = new Post(testo, id, user);
        Post createdPost = postService.savePost(post);
        if (file != null && !file.isEmpty()) {
            if(!checkImageFile(file)) {
                response.put("error", "Formato dell'immagine non valido");
                return ResponseEntity.status(400).body(response);
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
            int v = postService.saveImage(createdPost.getId(), post.getFile());
            System.out.println(createdPost.getId() + " ID DEL NUOVO POST" + v);
            if (v == 0) {
                response.put("error", "Errore nel salvataggio dell'immagine");
                return ResponseEntity.status(500).body(response);
            }
        }
        response.put("message", "Poste creato con successo");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/vote/{id}")
    public ResponseEntity<?> votePost(@PathVariable long id, @RequestParam boolean vote, @RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();
        String res = votePostFacade.votePost(id, vote, token);
        response.put("message", res);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/modify/{id}")
    public ResponseEntity<?> modifyPost(@PathVariable long id, @RequestParam String testo, @RequestParam(value = "file", required = false) MultipartFile file, @RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();
        token = token.replace("Bearer ", "");
        if (!isValidText(testo, testoRegex)) {
            response.put("error", "Formato del testo non valido");
            return ResponseEntity.status(400).body(response);
        }
        DecodedJWT decodedJWT = JwtUtil.JwtDecode(token);
        long idUtente = decodedJWT.getClaim("id").asLong();
        if(!postService.isCreationUser(idUtente, id)) {
            response.put("error", "Modifica del post non autorizzata");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        Post post = postService.getPost(id);
        post.setTesto(testo);
        postService.savePost(post);
        if (file != null && !file.isEmpty()) {
            if(!checkImageFile(file)) {
                response.put("error", "Formato dell'immagine non valido");
                return ResponseEntity.status(400).body(response);
            }
            Path path = Paths.get(directory);
            Path postPath = Paths.get(directory + "/" + post.getId() + "/");
            try {
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                if (!Files.exists(postPath)) {
                    Files.createDirectories(postPath);
                }
                Path oldFilePath = Paths.get(post.getFile());
                if (Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath);
                }
                String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
                Path filePath = postPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                post.setFile(filePath.toString());
                postService.saveImage(post.getId(), post.getFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        response.put("message", "La modifica del post è avvenuta con successo");
        return ResponseEntity.ok(response);
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