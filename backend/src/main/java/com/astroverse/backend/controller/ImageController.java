package com.astroverse.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private final String FILE_SPACE_ROOT = System.getProperty("user.dir") + "\\uploads";
    private final String FILE_POST_ROOT = System.getProperty("user.dir") + "\\uploads-post";

    @GetMapping("/space/{id}/{imageName}")
    public ResponseEntity<?> getSpaceImage(@PathVariable Long id, @PathVariable String imageName, @RequestHeader("Authorization") String token) {
        String path = FILE_SPACE_ROOT + "\\" + id + "\\" + imageName;
        Path filePath = Paths.get(path);
        if(!Files.exists(filePath)) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Immagine non esistente");
            return ResponseEntity.status(400).body(response);
        }
        File file = new File(path);
        try {
            byte[] image = FileUtils.readFileToByteArray(file);
            String type = Files.probeContentType(filePath);
            if(type.equals("image/jpeg")) {
                return ResponseEntity.status(200).contentType(MediaType.IMAGE_JPEG).body(image);
            } else {
                return ResponseEntity.status(200).contentType(MediaType.IMAGE_PNG).body(image);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/post/{id}/{imageName}")
    public ResponseEntity<?> getPostImage(@PathVariable Long id, @PathVariable String imageName, @RequestHeader("Authorization") String token) {
        String path = FILE_POST_ROOT + "\\" + id + "\\" + imageName;
        File file = new File(path);
        Path filePath = Paths.get(path);
        try {
            byte[] image = FileUtils.readFileToByteArray(file);
            String type = Files.probeContentType(filePath);
            if (type.equals("image/jpeg")) {
                return ResponseEntity.status(200).contentType(MediaType.IMAGE_JPEG).body(image);
            } else {
                return ResponseEntity.status(200).contentType(MediaType.IMAGE_PNG).body(image);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}