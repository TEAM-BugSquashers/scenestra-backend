package com.bugsquashers.backend.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
//@AllArgsConstructor
@NoArgsConstructor
public class GenreMoviesDto {
    private Integer genreId;
    private String genreName;
    private String engName;
    private String videoUrl;
    private List<MovieDto> movies;      // ← Movie 엔티티가 아니라 MovieDto

    public GenreMoviesDto(Integer genreId, String genreName, String engName, String videoUrl, List<MovieDto> movies) {
        this.genreId   = genreId;
        this.genreName = genreName;
        this.engName   = engName;
        this.videoUrl = videoUrl;
        this.movies    = movies;
    }
}
