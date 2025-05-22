package com.bugsquashers.backend.movie.dto;

import com.bugsquashers.backend.movie.domain.Movie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    public MovieDto(Movie m) {
        this.movieId    = m.getMovieId();
        this.title      = m.getTitle();
        this.showTime   = m.getShowTime();
        this.director   = m.getDirector();
        this.openDate   = m.getOpenDate();
        this.numAudience= m.getNumAudience();
        this.posterUrl  = m.getPosterUrl();
    }

    // getters only...
}
