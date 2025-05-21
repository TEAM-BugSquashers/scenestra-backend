package com.bugsquashers.backend.movie.service;

import com.bugsquashers.backend.movie.domain.Genre;
import com.bugsquashers.backend.movie.domain.Movie;
import com.bugsquashers.backend.movie.dto.GenreMoviesDto;
import com.bugsquashers.backend.movie.dto.GenreResponse;
import com.bugsquashers.backend.movie.repository.GenreRepository;
import com.bugsquashers.backend.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.bugsquashers.backend.movie.dto.MovieDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    // 전체 영화 찾기
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // 전체 장르 목록 출력
    public List<GenreResponse> getAllGenres() {
        List<Genre> genres = genreRepository.findAll(); // 또는 N+1 방지된 쿼리 사용
        return genres.stream()
                .map(genre -> new GenreResponse(genre.getGenreId(), genre.getName(), genre.getVideoUrl()))
                .collect(Collectors.toList());
    }

    // 장르별 영화 찾기
    public List<GenreMoviesDto> getAllMoviesGroupedByGenre() {
        return genreRepository.findAll().stream()
                .map(g -> {
                    List<MovieDto> dtos = movieRepository.findAllByGenreName(g.getName()).stream()
                            .map(MovieDto::new)    // Movie → MovieDto 로 변환
                            .collect(Collectors.toList());
                    return new GenreMoviesDto(
                            g.getGenreId(),
                            g.getName(),
                            dtos
                    );
                })
                .collect(Collectors.toList());
    }

    /** 장르 이름으로 영화 조회해서 MovieDto 리스트로 반환 */
    public List<MovieDto> getMoviesByGenreNameDto(String genreName) {
        return movieRepository.findAllByGenreName(genreName).stream()
                .map(MovieDto::new)
                .collect(Collectors.toList());
    }

    // NEW
    public List<MovieDto> getLatestMoviesDto() {
        return movieRepository
                .findAllByOrderByOpenDateDesc()      // openDate DESC
                .stream()
                .map(MovieDto::new)                  // Movie → MovieDto
                .collect(Collectors.toList());
    }

}
