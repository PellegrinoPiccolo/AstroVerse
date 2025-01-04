package com.astroverse.backend.controller;

import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.model.Post;
import com.astroverse.backend.service.PostService;
import com.astroverse.backend.service.UserService;
import com.astroverse.backend.service.VotePostFacade;
import com.astroverse.backend.service.VoteService;
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
    private final VoteService voteService;
    private static final String directory = "uploads-post/";

    private final VotePostFacade votePostFacade;

    public PostController(UserService userService, PostService postService, VoteService voteService, VotePostFacade votePostFacade) {
        this.userService = userService;
        this.postService = postService;
        this.voteService = voteService;
        this.votePostFacade = votePostFacade;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<?> createPost(@RequestParam String testo, @RequestParam(value = "file", required = false) MultipartFile file, @RequestHeader("Authorization") String token, @PathVariable long id) {
        token = token.replace("Bearer ", "");
        if(!isValidText(testo, testoRegex)) {
            return ResponseEntity.status(400).body("Formato del testo non valido");
        }
        DecodedJWT decoded = JwtUtil.JwtDecode(token);
        Post post = new Post(testo, id, decoded.getClaim("id").asLong());
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
            int v = postService.saveImage(createdPost.getId(), post.getFile());
            System.out.println(createdPost.getId() + " ID DEL NUOVO POST" + v);
            if (v == 0) {
                return ResponseEntity.status(500).body("Errore nel salvataggio dell'immagine");
            }
        }
        return ResponseEntity.ok("Nuovo post creato con successo");
    }

    @PostMapping("/vote/{id}")
    public ResponseEntity<?> votePost(@PathVariable long id, @RequestParam boolean vote, @RequestHeader("Authorization") String token) {
        String response = votePostFacade.votePost(id, vote, token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/modify/{id}")
    public ResponseEntity<?> modifyPost(@PathVariable long id, @RequestParam String testo, @RequestParam(value = "file", required = false) MultipartFile file, @RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        if (!isValidText(testo, testoRegex)) {
            return ResponseEntity.status(400).body("Formato del testo non valido");
        }
        DecodedJWT decodedJWT = JwtUtil.JwtDecode(token);
        long idUtente = decodedJWT.getClaim("id").asLong();
        if(!postService.isCreationUser(idUtente, id)) {
            return ResponseEntity.status(401).body("Modifica del post non autorizzata");
        }
        Post post = postService.getPost(id);
        post.setTesto(testo);
        postService.savePost(post);
        if (file != null && !file.isEmpty()) {
            if(!checkImageFile(file)) {
                return ResponseEntity.status(400).body("Formato dell'immagine non valido");
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
        return ResponseEntity.ok("La modifica al post è avvenuta con successo");
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