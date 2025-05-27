package com.bugsquashers.backend.theater;


import com.bugsquashers.backend.theater.domain.Theater;
import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("{theaterId}")
    @Operation(summary = "상영관 상세 조회", description = "특정 상영관의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getTheaterById(@PathVariable int theaterId) {
        Theater theater = theaterService.getTheaterById(theaterId);
        return ApiResponse.onSuccess(SuccessStatus.OK, theater);
    }
}
