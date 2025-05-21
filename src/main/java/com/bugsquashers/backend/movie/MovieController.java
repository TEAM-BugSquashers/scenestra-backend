package com.bugsquashers.backend.movie;

import com.bugsquashers.backend.movie.dto.MovieDto;
import com.bugsquashers.backend.movie.service.MovieService;
import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;


    @GetMapping("/movie")
    public ResponseEntity<ApiResponse<Object>> allMovies() {
        //리턴값 SuccessStatus.OK 뒤에 , 넣고 넣어주기
        return ApiResponse.onSuccess(SuccessStatus.OK, movieService.getAllMovies());
    }

    @GetMapping("/genre")
    public ResponseEntity<ApiResponse<Object>> allGenres() {
        return ApiResponse.onSuccess(SuccessStatus.OK, movieService.getAllGenres());
    }

    @GetMapping("/genre/movie")
    public ResponseEntity<ApiResponse<Object>> allMoviesByGenre() {
        return ApiResponse.onSuccess(SuccessStatus.OK, movieService.getAllMoviesGroupedByGenre());
    }

    // 추가: 장르 이름으로 영화 조회
    /** /api/genre/{name}/movie → MovieDto 리스트로 내려줌 */
    @GetMapping("/genre/{name}/movie")
    public ResponseEntity<ApiResponse<Object>> moviesByGenreName(@PathVariable String name) {
        List<MovieDto> dtos = movieService.getMoviesByGenreNameDto(name);
        return ApiResponse.onSuccess(SuccessStatus.OK, dtos);
    }

}

