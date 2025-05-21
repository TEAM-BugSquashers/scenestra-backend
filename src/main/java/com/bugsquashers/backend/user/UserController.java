package com.bugsquashers.backend.user;

import com.bugsquashers.backend.user.dto.UserJoinRequest;
import com.bugsquashers.backend.user.dto.UserJoinResponse;
import com.bugsquashers.backend.user.service.UserService;
import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 API")
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "새로운 회원을 생성합니다.")
    public ResponseEntity<ApiResponse<Object>> joinUser(@Valid @RequestBody UserJoinRequest reqDto) {
        UserJoinResponse res = userService.userJoin(reqDto);
        return ApiResponse.onSuccess(SuccessStatus.OK, res);
    }

    @GetMapping("/check-username")
    @Operation(summary = "아이디 중복 체크", description = "아이디 중복 체크를 합니다.")
    public ResponseEntity<ApiResponse<Object>> checkDuplicateUsername(@RequestParam String username) {
        userService.validateDuplicateUsername(username);
        return ApiResponse.onSuccess(SuccessStatus.OK, "사용 가능합니다.");
    }

    @GetMapping("/preferred-genres")
    @Operation(summary = "선호 장르 조회", description = "유저의 선호 장르를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getPreferredGenres(@AuthenticationPrincipal UserPrincipal principal) {

        return ApiResponse.onSuccess(SuccessStatus.OK, userService.userPreferredGenres(principal.getUserId()));

    }
}
