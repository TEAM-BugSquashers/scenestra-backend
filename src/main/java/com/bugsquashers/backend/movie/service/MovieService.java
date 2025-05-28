package com.bugsquashers.backend.movie.service;

import com.bugsquashers.backend.movie.domain.Genre;
import com.bugsquashers.backend.movie.domain.Movie;
import com.bugsquashers.backend.movie.dto.GenreMoviesDto;
import com.bugsquashers.backend.movie.dto.GenreResponse;
import com.bugsquashers.backend.movie.dto.MovieDto;
import com.bugsquashers.backend.movie.dto.MovieDto2;
import com.bugsquashers.backend.movie.repository.GenreRepository;
import com.bugsquashers.backend.movie.repository.MovieRepository;
import com.bugsquashers.backend.user.domain.User;
import com.bugsquashers.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;

    // 전체 영화 찾기
    @Transactional(readOnly = true)
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // 영화 상세 정보
    @Transactional(readOnly = true)
    public MovieDto2 getMovieById(String movieId) {
        Movie m = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("해당 영화를 찾지 못했습니다.: " + movieId));
        return new MovieDto2(m);
    }

    // 전체 장르 목록 출력
    @Transactional(readOnly = true)
    public List<GenreResponse> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return genres.stream()
                .map(genre -> new GenreResponse(genre.getGenreId(), genre.getName(), genre.getEngName(), genre.getVideoUrl()))
                .toList();
    }

    // 장르별 영화 찾기 - 20개씩
    // 갯수제한걸기 (20개)
    @Transactional(readOnly = true)
    public List<GenreMoviesDto> getTop20MoviesPerGenre() {
        return genreRepository.findAll().stream()
                .map(g -> {
                    List<MovieDto> DTOs = movieRepository
                            .findTop20ByGenreId(g.getGenreId())
                            .stream()
                            .map(MovieDto::new)
                            .collect(Collectors.toList());
                    return new GenreMoviesDto(
                            g.getGenreId(),
                            g.getName(),
                            g.getEngName(),
                            g.getVideoUrl(),
                            DTOs
                    );
                })
                .toList();
    }

    // 장르 id 로 영화 조회 - 해당 장르의 영화 전체
    // 갯수제한 풀기
    @Transactional(readOnly = true)
    public List<GenreMoviesDto> getMovieByGenreId(int genreId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("해당 장르를 찾지 못했습니다.: " + genreId));

        List<MovieDto> movieDTOs = movieRepository
                .findAllByGenreId(genreId)
                .stream()
                .map(MovieDto::new)
                .toList();

        GenreMoviesDto resultDto = new GenreMoviesDto(
                genre.getGenreId(),
                genre.getName(),
                genre.getEngName(),
                genre.getVideoUrl(),
                movieDTOs
        );

        return Collections.singletonList(resultDto);
    }

    // 추천 페이지용
    @Transactional(readOnly = true)
    public List<MovieDto> getTopNByGenreId(int genreId, int n) {
        Pageable page = PageRequest.of(0, n);
        return movieRepository
                .findTopNByGenreId(genreId, page)
                .stream()
                .map(MovieDto::new)
                .toList();
    }


    // NEW
    public List<MovieDto> getLatestMoviesDto(int n) {
        return movieRepository
                .findAllByOrderByOpenDateDesc(PageRequest.of(0, n))
                .stream()
                .map(MovieDto::new)
                .toList();
    }

    // Best
    public List<MovieDto> getMostPopularMoviesDto(int n) {
        return movieRepository.findAllByOrderByNumAudienceDesc(PageRequest.of(0, n))
                .stream()
                .map(MovieDto::new)
                .toList();
    }

    // Recommend
    @Transactional(readOnly = true)
    public Map<String,Object> getRecommendations(Long userId, int n) {
        // 선호 장르 ID 추출
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Integer> genreIds = user.getUserGenres().stream()
                .map(userGenre -> userGenre.getGenre().getGenreId())
                .toList();

        // 장르별 영화 묶음 생성
        List<GenreMoviesDto> byGenre = genreIds.stream()
                .map(gid -> {
                    Genre g = genreRepository.findById(gid)
                            .orElseThrow(() -> new EntityNotFoundException("Genre not found: " + gid));
                    List<MovieDto> DTOs = getTopNByGenreId(gid, n);
                    return new GenreMoviesDto(
                            g.getGenreId(),
                            g.getName(),
                            g.getEngName(),
                            g.getVideoUrl(),
                            DTOs
                    );
                })
                .toList();

        // 최신, 인기
        List<MovieDto> newTop  = getLatestMoviesDto(n);
        List<MovieDto> bestTop = getMostPopularMoviesDto(n);

        Map<String,Object> result = new LinkedHashMap<>();
        result.put("genreMovies", byGenre);
        result.put("newMovies",    newTop);
        result.put("bestMovies",   bestTop);
        return result;
    }

    // search
    @Transactional(readOnly = true)
    public List<MovieDto> searchByTitle(String keyword) {
        return movieRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(MovieDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public Movie getMovieByMovieId(String movieId) {
        return movieRepository.findByMovieId(movieId)
                .orElseThrow(() -> new EntityNotFoundException("해당 영화를 찾지 못했습니다.: " + movieId));
    }
}
