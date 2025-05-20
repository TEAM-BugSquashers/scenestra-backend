package com.bugsquashers.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UserJoinRequest {
    @NotBlank(message = "사용자 이름은 필수입니다")
    @Size(min = 4, max = 20, message = "사용자 이름은 4자 이상 20자 이하여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "사용자 이름은 영문자, 숫자, 언더스코어만 사용 가능합니다")
    private String username;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하여야 합니다")
    //@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
    //        message = "비밀번호는 숫자, 소문자, 대문자, 특수문자를 각각 1개 이상 포함해야 합니다")
    private String password;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이 아닙니다")
    private String email;
    
    @NotBlank(message = "휴대폰 번호는 필수입니다")
    @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$", 
            message = "유효한 휴대폰 번호 형식이 아닙니다")
    private String mobile;
    
    @NotBlank(message = "실명은 필수입니다")
    @Size(min = 2, max = 30, message = "실명은 2자 이상 30자 이하여야 합니다")
    private String realName;
}
