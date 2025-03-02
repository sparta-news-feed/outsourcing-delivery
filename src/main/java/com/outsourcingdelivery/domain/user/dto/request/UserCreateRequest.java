package com.outsourcingdelivery.domain.user.dto.request;

import com.outsourcingdelivery.common.Const;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCreateRequest {

    @Email
    @NotBlank(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    @Pattern(
        regexp = Const.PASSWORD_PATTERN,
        message = "비밀번호 형식이 올바르지 않습니다."
    )
    private String password;

    @NotBlank(message = "유저이름 입력은 필수입니다.")
    private String username;

    @NotBlank(message = "유저 타입 정보는 필수입니다.")
    private String userType;

    @NotBlank(message = "핸드폰 번호 입력은 필수입니다.")
    private String phoneNumber;

    @NotBlank(message = "주소 입력은 필수입니다.")
    private String address;

    @Builder
    private UserCreateRequest(String email, String password, String username, String userType, String phoneNumber, String address) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.userType = userType;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
