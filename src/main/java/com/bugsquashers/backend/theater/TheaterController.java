package com.bugsquashers.backend.theater;


import com.bugsquashers.backend.theater.domain.Theater;
import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
@Tag(name = "Theater", description = "상영관 API")
public class TheaterController {
    private final TheaterService theaterService;

    @GetMapping()
    @Operation(summary = "상영관 목록 전체 조회", description = "상영관 목록 전체를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getAllTheaters() {
        return ApiResponse.onSuccess(SuccessStatus.OK, theaterService.getAllTheaters());
    }

    @GetMapping("/capacity")
    @Operation(summary = "수용 가능 상영관 목록 조회", description = "해당 인원을 수용 가능한 상영관 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getTheatersByCapacity(@RequestParam int num) {
        return ApiResponse.onSuccess(SuccessStatus.OK, theaterService.findTheatersByCapacity(num));
    }
}
