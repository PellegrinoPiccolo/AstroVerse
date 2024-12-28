package com.astroverse.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name = "user")
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean isAdmin;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<UserSpace> userSpaces = new HashSet<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserPost> userPosts = new HashSet<>();
    private String nome;
    private String cognome;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    public User() {
        isAdmin = false;
    }

    public User(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public User(String nome, String cognome, String username, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
