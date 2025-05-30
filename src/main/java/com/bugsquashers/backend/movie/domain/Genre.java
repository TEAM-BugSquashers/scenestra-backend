package com.bugsquashers.backend.movie.domain;

import com.bugsquashers.backend.user.domain.UserGenre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer genreId;

    private String name;

    private String engName;

    private String videoUrl;

    @OneToMany(
            mappedBy = "genre",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<MovieGenre> movieGenres = new ArrayList<>();

    @OneToMany(
            mappedBy = "genre",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<UserGenre> userGenres = new ArrayList<>();
}
