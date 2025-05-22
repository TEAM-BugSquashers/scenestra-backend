package com.bugsquashers.backend.admin;

import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin(미구현)", description = "관리자 API")
public class AdminController {

    @GetMapping("/users")
    @Operation(summary = "모든 사용자 조회", description = "모든 사용자의 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> allUsers() {
        return ApiResponse.onSuccess(SuccessStatus.OK, "");
    }
}
