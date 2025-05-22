package com.bugsquashers.backend.movie.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MovieGenre implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private MovieGenreId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movieId")
    @JsonBackReference
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("genreId")
    @JsonBackReference
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public MovieGenre(Movie movie, Genre genre) {
        this.movie = movie;
        this.genre = genre;
        this.id = new MovieGenreId(movie.getMovieId(), genre.getGenreId());
    }
}

