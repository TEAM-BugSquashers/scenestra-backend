package com.bugsquashers.backend.movie.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer genreId;

    private String name;

    private String videoUrl;

    @OneToMany(
            mappedBy = "genre",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<MovieGenre> movieGenres = new ArrayList<>();
}
