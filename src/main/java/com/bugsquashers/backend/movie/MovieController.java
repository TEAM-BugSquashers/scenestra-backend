package com.bugsquashers.backend.movie;

import com.bugsquashers.backend.movie.service.MovieService;
import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;


    @GetMapping
    public ResponseEntity<ApiResponse<Object>> allMovies() {
        movieService.getAllMovies();

        //리턴값 SuccessStatus.OK 뒤에 , 넣고 넣어주기
        return ApiResponse.onSuccess(SuccessStatus.OK, "test");
    }

    @GetMapping("/genre")
    public ResponseEntity<ApiResponse<Object>> allGenres() {
        movieService.getAllGenres();

        //리턴값 SuccessStatus.OK 뒤에 , 넣고 넣어주기
        return ApiResponse.onSuccess(SuccessStatus.OK, "test2");
    }
}

