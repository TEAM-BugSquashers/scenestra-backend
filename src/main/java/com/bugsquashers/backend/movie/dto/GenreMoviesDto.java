package com.bugsquashers.backend.movie.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GenreMoviesDto {
    private Integer genreId;
    private String genreName;
    private String engName;
    private String videoUrl;
    private List<MovieDto> movies;

    public GenreMoviesDto(Integer genreId, String genreName, String engName, String videoUrl, List<MovieDto> movies) {
        this.genreId   = genreId;
        this.genreName = genreName;
        this.engName   = engName;
        this.videoUrl = videoUrl;
        this.movies    = movies;
    }
}
