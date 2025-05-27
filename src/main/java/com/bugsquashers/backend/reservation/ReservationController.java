package com.bugsquashers.backend.reservation;

import com.bugsquashers.backend.reservation.domain.Reservation;
import com.bugsquashers.backend.reservation.dto.GetAvailableDatesRequest;
import com.bugsquashers.backend.reservation.dto.GetAvailableTimesRequest;
import com.bugsquashers.backend.reservation.dto.ReservationRequest;
import com.bugsquashers.backend.reservation.service.ReservationService;
import com.bugsquashers.backend.user.UserPrincipal;
import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    //특정 날짜에 예약 가능한 시간 목록 조회
    @GetMapping("/available-times")
    @Operation(summary = "특정 날짜에 예약 가능한 시간 목록 조회", description = "특정 상영관에서 특정 영화의 특정 날짜에 예약 가능한 시간 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getAvailableTimesInDay(@ParameterObject @Valid GetAvailableTimesRequest request) {
        return ApiResponse.onSuccess(SuccessStatus.OK, reservationService.getAvailableTimesInDayWithDetails(request.getTheaterId(), request.getMovieId(), request.getDay()));
    }

    //예약 가능 여부 최종 조회
    //예약 메서드와 같은 환경 및 경험을 제공하기 위해 Post 방식으로 구현
    @PostMapping("/check-availability")
    @Operation(summary = "상영관 예약 가능 여부 최종 조회", description = "특정 상영관에서 특정 영화의 특정 날짜/시간에 지정된 인원만큼 예약이 가능한지 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> checkAvailability(
            @RequestBody @Valid ReservationRequest request) {
        reservationService.checkReservationAvailability(
                request.getMovieId(),
                request.getTheaterId(),
                request.getDate(),
                request.getTime(),
                request.getNumPeople()
        );
        return ApiResponse.onSuccess(
                SuccessStatus.OK,
                "예약 가능합니다. 예약을 진행해주세요."
        );
    }

    @PostMapping()
    @Operation(summary = "상영관 예약", description = "특정 상영관에서 특정 영화의 특정 날짜/시간에 지정된 인원만큼 예약을 진행합니다.")
    public ResponseEntity<ApiResponse<Object>> createReservation(
            @RequestBody @Valid ReservationRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        Reservation reservation = reservationService.createReservation(
                request.getMovieId(),
                request.getTheaterId(),
                request.getDate(),
                request.getTime(),
                request.getNumPeople(),
                principal.getUserId()
        );
        return ApiResponse.onSuccess(
                SuccessStatus.CREATE,
                reservation
        );
    }


}
