package com.bugsquashers.backend.util.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus{
    OK(HttpStatus.OK,"OK","성공하였습니다."),
    CREATE(HttpStatus.CREATED, "CREATED","생성하였습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "LOGOUT_SUCCESS", "로그아웃 되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
