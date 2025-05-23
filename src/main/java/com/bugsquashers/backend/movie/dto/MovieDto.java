package com.bugsquashers.backend.movie.dto;

import com.bugsquashers.backend.movie.domain.Movie;
import com.bugsquashers.backend.movie.domain.MovieGenre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {
    private String movieId;
    private String title;
    private Integer showTime;
    private String director;
    private java.util.Date openDate;
    private Integer numAudience;
    private String posterUrl;
    private List<String> genreNames;

    public MovieDto(Movie m) {
        this.movieId    = m.getMovieId();
        this.title      = m.getTitle();
        this.showTime   = m.getShowTime();
        this.director   = m.getDirector();
        this.openDate   = m.getOpenDate();
        this.numAudience= m.getNumAudience();
        this.posterUrl  = m.getPosterUrl();
        this.genreNames = m.getMovieGenres().stream()
                .map(MovieGenre::getGenre)
                .map(g -> g.getName())
                .collect(Collectors.toList());
    }
}
