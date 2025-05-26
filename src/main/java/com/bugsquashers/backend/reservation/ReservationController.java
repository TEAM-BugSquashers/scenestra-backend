package com.bugsquashers.backend.reservation;

import com.bugsquashers.backend.reservation.dto.GetAvailableDatesRequest;
import com.bugsquashers.backend.reservation.dto.GetAvailableTimesRequest;
import com.bugsquashers.backend.reservation.service.ReservationService;
import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "예약 API")
public class ReservationController {
    private final ReservationService reservationService;

    //월별 상영관 예약가능일 목록 조회
    @GetMapping("/available-dates")
    @Operation(summary = "월별 상영관 예약 가능일 목록 조회", description = "특정 상영관에서 특정 영화의 월별 예약 가능한 날짜 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getAvailableDates(@ParameterObject @Valid GetAvailableDatesRequest request) {
        return ApiResponse.onSuccess(SuccessStatus.OK, reservationService.getAvailableDatesInMonth(request.getMovieId(), request.getTheaterId(), request.getYearMonth()));
    }

    @GetMapping("/available-times")
    @Operation(summary = "특정 날짜에 예약 가능한 시간 목록 조회", description = "특정 상영관에서 특정 영화의 특정 날짜에 예약 가능한 시간 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getAvailableTimesInDay(@ParameterObject @Valid GetAvailableTimesRequest request) {
        return ApiResponse.onSuccess(SuccessStatus.OK, reservationService.getAvailableTimesInDay(request.getTheaterId(), request.getMovieId(), request.getDay()));
    }

}
