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
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String argument;
    private String image;
    private String description;
    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL)
    private Set<UserSpace> userSpaces = new HashSet<>();
}