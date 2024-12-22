package com.astroverse.backend.component;

import com.astroverse.backend.model.UserPost;
import com.astroverse.backend.model.UserSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    private Set<UserSpace> userSpaces = new HashSet<>();
    private Set<UserPost> userPosts = new HashSet<>();
    private String nome;
    private String cognome;
    private String username;
    private String email;
}