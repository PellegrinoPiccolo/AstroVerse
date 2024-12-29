package com.astroverse.backend.controller;

import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.model.Post;
import com.astroverse.backend.model.User;
import com.astroverse.backend.model.Vote;
import com.astroverse.backend.service.PostService;
import com.astroverse.backend.service.UserService;
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
import java.util.Optional;
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

    public PostController(UserService userService, PostService postService, VoteService voteService) {
        this.userService = userService;
        this.postService = postService;
        this.voteService = voteService;
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
            if(postService.saveImage(post.getId(), post.getFile()) == 0) {
                return ResponseEntity.status(500).body("Errore nel salvataggio dell'immagine");
            }
        }
        return ResponseEntity.ok("Nuovo post creato con successo");
    }

    @PostMapping("/vote/{id}")
    public ResponseEntity<?> votePost(@PathVariable long id, @RequestParam boolean vote, @RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        DecodedJWT decodedJWT = JwtUtil.JwtDecode(token);
        String email = decodedJWT.getClaim("email").asString();
        User user = userService.getUser(email);
        Post post = postService.getPost(id);
        Optional<Vote> optionalVote = voteService.existVote(user.getId(), post.getSpaceId());
        if (optionalVote.isPresent()) {
           Vote oldVote = optionalVote.get();
           if (oldVote.isVote() != vote) {
                voteService.updateVote(oldVote.getId(), vote);
               return ResponseEntity.ok("Voto aggiornato");
           } else {
               voteService.deleteVote(oldVote.getId());
               return ResponseEntity.ok("Voto eliminato");
           }
        }
        Vote newVote = new Vote(post, user, vote);
        voteService.saveVote(newVote);
        return ResponseEntity.ok("Votazione al post effettuata con successo");
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