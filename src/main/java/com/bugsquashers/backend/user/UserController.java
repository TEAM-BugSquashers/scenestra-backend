package com.bugsquashers.backend.user;

import com.bugsquashers.backend.user.dto.UserInfoUpdateRequest;
import com.bugsquashers.backend.user.dto.UserJoinRequest;
import com.bugsquashers.backend.user.dto.UserJoinResponse;
import com.bugsquashers.backend.user.dto.UserPasswordUpdateRequest;
import com.bugsquashers.backend.user.dto.UserInfoResponse;
import com.bugsquashers.backend.user.service.UserService;
import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User", description = "유저 API")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "내 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.onSuccess(SuccessStatus.OK, userService.getUserInfo(principal.getUserId()));
    }

    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "새로운 회원을 생성합니다.")
    public ResponseEntity<ApiResponse<Object>> joinUser(@Valid @RequestBody UserJoinRequest reqDto) {
        UserJoinResponse res = userService.userJoin(reqDto);
        return ApiResponse.onSuccess(SuccessStatus.CREATE, res);
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

    @PutMapping("/preferred-genres")
    @Operation(summary = "선호 장르 수정", description = "유저의 선호 장르를 수정합니다.")
    public ResponseEntity<ApiResponse<Object>> updatePreferredGenres(@AuthenticationPrincipal UserPrincipal principal,
                                                                     @RequestBody @Size(min = 3, max = 3, message = "선호 장르를 3가지 선택해야 합니다.")
                                                                     Set<Integer> genres) {
        userService.updateUserPreferredGenres(principal.getUserId(), genres);
        return ApiResponse.onSuccess(SuccessStatus.OK, "선호 장르가 수정되었습니다.");
    }

    @PutMapping("/info")
    @Operation(summary = "유저 정보 수정", description = "유저 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<Object>> updateUserInfo(@AuthenticationPrincipal UserPrincipal principal,
                                                                     @RequestBody @Valid UserInfoUpdateRequest reqDto) {
        userService.updateUserInfo(principal.getUserId(), reqDto);
        return ApiResponse.onSuccess(SuccessStatus.OK, "유저 정보가 수정되었습니다.");
    }

    @PutMapping("/password")
    @Operation(summary = "비밀번호 수정", description = "유저 비밀번호를 수정합니다.")
    public ResponseEntity<ApiResponse<Object>> updatePassword(@AuthenticationPrincipal UserPrincipal principal,
                                                                     @RequestBody @Valid UserPasswordUpdateRequest reqDto) {
        userService.updatePassword(principal.getUserId(), reqDto);
        return ApiResponse.onSuccess(SuccessStatus.OK, "비밀번호가 수정되었습니다.");
    }
}
