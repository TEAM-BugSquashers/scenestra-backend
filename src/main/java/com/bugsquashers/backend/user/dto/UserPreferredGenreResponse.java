package com.bugsquashers.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPreferredGenreResponse {
    private Integer genreId;
    private String genreName;
}
