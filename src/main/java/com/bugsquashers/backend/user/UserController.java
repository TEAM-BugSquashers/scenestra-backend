package com.bugsquashers.backend.user;

import com.bugsquashers.backend.user.dto.UserJoinRequest;
import com.bugsquashers.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 API")
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "새로운 회원을 생성합니다.")
    public ResponseEntity<String> joinUser(@Valid @RequestBody UserJoinRequest reqDto) {
        userService.userJoin(reqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("계정이 생성되었습니다.");
    }
}
