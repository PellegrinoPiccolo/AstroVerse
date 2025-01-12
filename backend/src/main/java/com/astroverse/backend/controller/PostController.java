package com.astroverse.backend.controller;

import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.model.Post;
import com.astroverse.backend.model.User;
import com.astroverse.backend.model.Vote;
import com.astroverse.backend.service.PostService;
import com.astroverse.backend.service.VotePostFacade;
import com.astroverse.backend.service.VoteService;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/post")
public class PostController {
    private static final String testoRegex = "^[\\w\\s\\p{Punct}]{1,400}$";
    private final PostService postService;
    private final VoteService voteService;
    private static final String directory = "uploads-post/";
    private final VotePostFacade votePostFacade;

    public PostController(PostService postService, VotePostFacade votePostFacade, VoteService voteService) {
        this.postService = postService;
        this.votePostFacade = votePostFacade;
        this.voteService = voteService;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<?> createPost(@RequestParam String testo, @RequestParam(value = "file", required = false) MultipartFile file, @RequestHeader("Authorization") String token, @PathVariable long id) {
        Map<String, Object> response = new HashMap<>();
        token = token.replace("Bearer ", "");
        if (!isValidText(testo, testoRegex)) {
            System.out.println("PROVA QUA");
            response.put("error", "Formato del testo non valido");
            return ResponseEntity.status(400).body(response);
        }
        DecodedJWT decoded = JwtUtil.JwtDecode(token);
        User user = new User(decoded.getClaim("id").asLong());
        Post post = new Post(testo, id, user);
        Post createdPost = postService.savePost(post);
        if (file != null && !file.isEmpty()) {
            if(!checkImageFile(file)) {
                System.out.println("PROVA QUA 2");
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
            if (v == 0) {
                response.put("error", "Errore nel salvataggio dell'immagine");
                return ResponseEntity.status(500).body(response);
            }
        }
        response.put("message", "Poste creato con successo");
        response.put("newPost", createdPost);
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
    public ResponseEntity<?> modifyPost(@PathVariable long id, @RequestParam String testo, @RequestParam(value = "file", required = false) MultipartFile file, @RequestHeader("Authorization") String token, @RequestParam(value = "imageDeleted", required = false) boolean imageDeleted) {
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
        if(imageDeleted) {
            Path oldFilePath = Paths.get(post.getFile());
            if (Files.exists(oldFilePath)) {
                try {
                    Files.delete(oldFilePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            postService.saveImage(post.getId(), null);
        } else if (file != null && !file.isEmpty()) {
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
                if(post.getFile() != null) {
                    Path oldFilePath = Paths.get(post.getFile());
                    if (Files.exists(oldFilePath)) {
                        Files.delete(oldFilePath);
                    }
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

    @GetMapping("/get-vote/{postId}")
    public ResponseEntity<?> getVote(@PathVariable long postId, @RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        Map<String, Boolean> response = new HashMap<>();
        DecodedJWT decodedJWT = JwtUtil.JwtDecode(token);
        long userId = decodedJWT.getClaim("id").asLong();
        Optional<Vote> optionalVote = voteService.existVote(userId, postId);
        if(optionalVote.isPresent()) {
            Vote vote = optionalVote.get();
            response.put("message", vote.isVote());
        } else {
            response.put("message", null);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-posts/{page}")
    public ResponseEntity<?> getAllPosts(@PathVariable int page) {
        Map<String, Object> response = new HashMap<>();
        int limit = 40;
        int offset = (page-1)*limit;
        Page<Post> posts = postService.getAllPosts(limit, offset);
        List<Post> postList = posts.getContent().stream().toList();
        for (Post post : posts) {
            post.setUserData(new User(post.getUser().getId(),
                    post.getUser().getNome(),
                    post.getUser().getCognome(),
                    post.getUser().getUsername(),
                    post.getUser().getEmail())
            );
        }
        long totalPosts = postService.getNumberOfAllPosts();
        long numberOfPages = (long) Math.ceil((double) totalPosts/limit);
        response.put("posts", postList);
        response.put("numberOfPages", numberOfPages);
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