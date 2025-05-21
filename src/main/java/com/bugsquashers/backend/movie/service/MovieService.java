package com.bugsquashers.backend.movie.service;

import com.bugsquashers.backend.movie.domain.Genre;
import com.bugsquashers.backend.movie.domain.Movie;
import com.bugsquashers.backend.movie.repository.GenreRepository;
import com.bugsquashers.backend.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;


    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

}
