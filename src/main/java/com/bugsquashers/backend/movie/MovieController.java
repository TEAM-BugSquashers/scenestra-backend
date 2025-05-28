package com.bugsquashers.backend.movie;

import com.bugsquashers.backend.movie.dto.GenreMoviesDto;
import com.bugsquashers.backend.movie.dto.MovieDto;
import com.bugsquashers.backend.movie.dto.MovieDto2;
import com.bugsquashers.backend.movie.service.MovieService;
import com.bugsquashers.backend.user.UserPrincipal;
import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movie", description = "영화 및 장르 API")
public class MovieController {
    private final MovieService movieService;

    // 전체 영화
    @GetMapping()
    @Operation(summary = "전체 영화 조회", description = "전체 영화를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> allMovies() {
        //리턴값 SuccessStatus.OK 뒤에 , 넣고 넣어주기
        return ApiResponse.onSuccess(SuccessStatus.OK, movieService.getAllMovies());
    }

    // 영화 상세 정보
    @Operation(summary = "영화 상세 정보", description = "movieId를 이용해 해당 영화의 정보를 조회합니다.")
    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<Object>> getMovie(@PathVariable String movieId) {

        MovieDto2 dto = movieService.getMovieById(movieId);
        return ApiResponse.onSuccess(SuccessStatus.OK, dto);
    }

    // 장르 목록
    @GetMapping("/genres")
    @Operation(summary = "전체 장르 조회", description = "전체 장르를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> allGenres() {
        return ApiResponse.onSuccess(SuccessStatus.OK, movieService.getAllGenres());
    }

    // 장르별 영화 찾기 - 장르별 20개 씩
    @GetMapping("/grouped-by-genre")
    @Operation(summary = "장르별 영화 조회", description = "장르별 그룹화된 영화 목록을 조회합니다.(최대 20개)")
    public ResponseEntity<ApiResponse<Object>> allMoviesByGenre() {
        return ApiResponse.onSuccess(SuccessStatus.OK, movieService.getTop20MoviesPerGenre());
    }

    // 장르 id 으로 영화 조회 - 해당 장르의 영화 전체
    @GetMapping("/genres/{genreId}")
    @Operation(summary = "장르별 전체 영화 조회", description = "장르별 전체 영화를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> moviesByGenreName(@PathVariable int genreId) {
        List<GenreMoviesDto> DTOs = movieService.getMovieByGenreId(genreId);
        return ApiResponse.onSuccess(SuccessStatus.OK, DTOs);
    }

    // New
    @GetMapping("/new")
    @Operation(summary = "새로운 영화 조회", description = "최신 영화를 조회합니다.(20개)")
    public ResponseEntity<ApiResponse<Object>> newestMovies() {
        List<?> newMovie = movieService.getLatestMoviesDto(20);
        return ApiResponse.onSuccess(SuccessStatus.OK, newMovie);
    }

    //Best
    @GetMapping("/best")
    @Operation(summary = "인기 영화 조회", description = "인기 영화를 조회합니다.(20개)")
    public ResponseEntity<ApiResponse<Object>> popularMovies() {
        List<MovieDto> bestMovie = movieService.getMostPopularMoviesDto(20);
        return ApiResponse.onSuccess(SuccessStatus.OK, bestMovie);
    }

    // 추천 페이지
    @GetMapping("/recommend")
    @Operation(summary = "추천 페이지", description = "선호 장르(3개), 최신 영화, 인기 영화를 조회합니다.(5개)")
    public ResponseEntity<ApiResponse<Object>> recommendMovies(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(name="n", defaultValue="7") int n) {
        Map<String,Object> payload =
                movieService.getRecommendations(principal.getUserId(), n);

        return ApiResponse.onSuccess(SuccessStatus.OK, payload);
    }

    // 검색
    @GetMapping("/search")
    @Operation(summary = "검색", description = "영화명으로 영화를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> searchMovies(
            @RequestParam("title") String keyword) {
        List<MovieDto> payload = movieService.searchByTitle(keyword);
        return ApiResponse.onSuccess(SuccessStatus.OK, payload);
    }
}

