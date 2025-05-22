package com.bugsquashers.backend.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class UserInfoUpdateRequest {
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "휴대폰 번호는 필수입니다")
    @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$",
            message = "유효한 휴대폰 번호 형식이 아닙니다")
    private String mobile;
}
