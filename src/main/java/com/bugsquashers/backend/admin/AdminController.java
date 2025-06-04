package com.bugsquashers.backend.admin;

import com.bugsquashers.backend.reservation.dto.ReservationDetailsResponse;
import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin(미구현)", description = "관리자 API")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "모든 사용자 조회", description = "모든 사용자의 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> allUsers() {
        return ApiResponse.onSuccess(SuccessStatus.OK, adminService.getAllUsers());
    }

    @GetMapping("/reservations")
    @Operation(summary = "모든 예약 조회", description = "예약 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getAllReservations() {
        return ApiResponse.onSuccess(SuccessStatus.OK, adminService.getAllReservations());
    }

    @GetMapping("/reservations/{reservationId}")
    @Operation(summary = "예약 상세 조회", description = "해당 예약의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getReservationDetailsById(@PathVariable Integer reservationId) {
        return ApiResponse.onSuccess(SuccessStatus.OK, adminService.getReservationDetailsById(reservationId));
    }
}
