package com.bugsquashers.backend.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenreResponse {
    private Integer genreId;
    private String name;
    private String videoUrl;
}

