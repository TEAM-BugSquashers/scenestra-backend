package com.bugsquashers.backend.movie.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties("movieGenres")
public class Movie {
    @Id
    private String movieId;

    @Column(nullable = false)
    private String title;

    private Integer showTime;

    private String director;

    @Temporal(TemporalType.TIMESTAMP)
    private Date openDate;

    private Integer numAudience;

    @Column(columnDefinition = "TEXT")
    private String posterUrl;

    @OneToMany(
            mappedBy = "movie",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<MovieGenre> movieGenres = new ArrayList<>();
}
