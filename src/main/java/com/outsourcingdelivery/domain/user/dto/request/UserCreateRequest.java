package com.outsourcingdelivery.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCreateRequest {

    @Email
    @NotBlank(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String password;

    @NotBlank(message = "유저이름 입력은 필수입니다.")
    private String username;

    @NotBlank(message = "유저 타입 정보는 필수입니다.")
    private String userType;

    @NotBlank(message = "주소 입력은 필수입니다.")
    private String city;

    @NotBlank(message = "주소 입력은 필수입니다.")
    private String district;

    @NotBlank(message = "주소 입력은 필수입니다.")
    private String neighborhood;

}
