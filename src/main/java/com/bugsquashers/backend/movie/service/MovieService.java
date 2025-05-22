package com.bugsquashers.backend.movie.service;

import com.bugsquashers.backend.movie.domain.Genre;
import com.bugsquashers.backend.movie.domain.Movie;
import com.bugsquashers.backend.movie.dto.GenreMoviesDto;
import com.bugsquashers.backend.movie.dto.GenreResponse;
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
import com.bugsquashers.backend.movie.dto.MovieDto;

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
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // 영화 상세 정보
    public MovieDto getMovieById(String movieId) {
        Movie m = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found: " + movieId));
        return new MovieDto(m);
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
                            .map(MovieDto::new)
                            .collect(Collectors.toList());
                    return new GenreMoviesDto(
                            g.getGenreId(),
                            g.getName(),
                            g.getEngName(),
                            g.getVideoUrl(),
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

    // Recommend
    public Map<String,Object> getRecommendations(Long userId, int n) {
        // 1) 선호 장르 ID 추출
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Integer> genreIds = user.getUserGenres().stream()
                .map(ug -> ug.getGenre().getGenreId())
                .collect(Collectors.toList());

        // 2) 장르별 영화 묶음 생성
        List<GenreMoviesDto> byGenre = genreIds.stream()
                .map(gid -> {
                    Genre g = genreRepository.findById(gid)
                            .orElseThrow(() -> new EntityNotFoundException("Genre not found: " + gid));
                    List<MovieDto> dtos = getTopNByGenreId(gid, n);
                    return new GenreMoviesDto(
                            g.getGenreId(),
                            g.getName(),
                            g.getEngName(),
                            g.getVideoUrl(),
                            dtos
                    );
                })
                .collect(Collectors.toList());

        // 3) 최신 Top-n, 인기 Top-n
        List<MovieDto> newTop  = getLatestMoviesDto(n);
        List<MovieDto> bestTop = getMostPopularMoviesDto(n);

        // 4) Map에 담아서 한 번에 리턴
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("genreMovies", byGenre);
        result.put("newMovies",    newTop);
        result.put("bestMovies",   bestTop);
        return result;
    }

    // search
    public List<MovieDto> searchByTitle(String keyword) {
        return movieRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(MovieDto::new)
                .collect(Collectors.toList());
    }
}
