package com.bugsquashers.backend.user;

import com.bugsquashers.backend.user.dto.UserJoinRequest;
import com.bugsquashers.backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<String> joinUser(@Valid @RequestBody UserJoinRequest reqDto) {
        userService.userJoin(reqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("계정이 생성되었습니다.");
    }
}
