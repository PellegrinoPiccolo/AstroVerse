package com.astroverse.backend.component;

import com.astroverse.backend.model.Post;
import com.astroverse.backend.model.UserSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class UserDTO {
    private Set<UserSpace> userSpaces = new HashSet<>();
    private Set<Post> userPosts = new HashSet<>();
    private String nome;
    private String cognome;
    private String username;
    private String email;
}