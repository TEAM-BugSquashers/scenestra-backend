package com.bugsquashers.backend.movie.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Movie {
    @Id
    private String movieId;

    @Column(nullable = false)
    private String title;

    private Integer showTime;

    private String director;

    private Date openDate;

    private String posterUrl;

    @OneToMany(
            mappedBy = "movie",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<MovieGenre> movieGenres = new ArrayList<>();
}
