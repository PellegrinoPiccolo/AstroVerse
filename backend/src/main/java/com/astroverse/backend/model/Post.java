package com.astroverse.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String testo;
    private String file;
    private int upVoti;
    private int downVoti;
    private int spaceId;
    private int userId;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<UserPost> userPosts = new HashSet<>();
}
