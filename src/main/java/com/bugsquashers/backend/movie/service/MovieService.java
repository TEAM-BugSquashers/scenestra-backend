package com.bugsquashers.backend.movie.service;

import com.bugsquashers.backend.movie.domain.Genre;
import com.bugsquashers.backend.movie.domain.Movie;
import com.bugsquashers.backend.movie.dto.GenreResponse;
import com.bugsquashers.backend.movie.repository.GenreRepository;
import com.bugsquashers.backend.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;


    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // GenreService.java (또는 해당 로직이 있는 서비스)
    public List<GenreResponse> getAllGenres() {
        List<Genre> genres = genreRepository.findAll(); // 또는 N+1 방지된 쿼리 사용
        return genres.stream()
                .map(genre -> new GenreResponse(genre.getGenreId(), genre.getName(), genre.getVideoUrl()))
                .collect(Collectors.toList());
    }
}
