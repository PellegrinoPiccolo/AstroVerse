package com.astroverse.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String argument;
    private String image;
    private String description;
    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<UserSpace> userSpaces = new HashSet<>();

    public Space(String title, String argument, String description) {
        this.title = title;
        this.argument = argument;
        this.image = "";
        this.description = description;
        this.userSpaces = new HashSet<>();
    }
}