package com.bugsquashers.backend.movie.service;

import com.bugsquashers.backend.movie.domain.Genre;
import com.bugsquashers.backend.movie.domain.Movie;
import com.bugsquashers.backend.movie.dto.GenreMoviesDto;
import com.bugsquashers.backend.movie.dto.GenreResponse;
import com.bugsquashers.backend.movie.repository.GenreRepository;
import com.bugsquashers.backend.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                .map(genre -> new GenreResponse(genre.getGenreId(), genre.getName(), genre.getEngName(), genre.getVideoUrl()))
                .collect(Collectors.toList());
    }

    // 장르별 영화 찾기 (장르 클릭 시 해당 장르의 영화 전체 출력하게 하기)
    public List<GenreMoviesDto> getAllMoviesGroupedByGenre() {
        return genreRepository.findAll().stream()
                .map(g -> {
                    List<MovieDto> dtos = movieRepository.findAllByGenreId(g.getGenreId()).stream()
                            .map(MovieDto::new)    // Movie → MovieDto 로 변환
                            .collect(Collectors.toList());
                    return new GenreMoviesDto(
                            g.getGenreId(),
                            g.getName(),
                            g.getEngName(),
                            dtos
                    );
                })
                .collect(Collectors.toList());
    }

    // 장르 id 로 영화 조회
    public List<MovieDto> getTopNByGenreId(int genreId, int n) {
        Pageable page = PageRequest.of(0, n);
        return movieRepository
                .findTopNByGenreId(genreId, page)
                .stream()
                .map(MovieDto::new)
                .collect(Collectors.toList());
    }

    // NEW
    public List<MovieDto> getLatestMoviesDto(int n) {
        return movieRepository
                .findAllByOrderByOpenDateDesc(PageRequest.of(0, n))
                .stream()
                .map(MovieDto::new)
                .collect(Collectors.toList());
    }

    // Best
    public List<MovieDto> getMostPopularMoviesDto(int n) {
        return movieRepository.findAllByOrderByNumAudienceDesc(PageRequest.of(0, n))
                .stream()
                .map(MovieDto::new)
                .collect(Collectors.toList());
    }
}
