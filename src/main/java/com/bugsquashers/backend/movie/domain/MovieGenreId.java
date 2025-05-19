package com.bugsquashers.backend.movie.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class MovieGenreId implements Serializable {
    @Column(name = "movie_id")
    private String movieId;

    @Column(name = "genre_id")
    private Integer genreId;

    public MovieGenreId(String movieId, Integer genreId) {
        this.movieId = movieId;
        this.genreId = genreId;
    }
}
