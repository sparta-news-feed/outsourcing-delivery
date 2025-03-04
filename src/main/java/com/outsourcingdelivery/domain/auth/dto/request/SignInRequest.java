package com.outsourcingdelivery.domain.auth.dto.request;

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
public class SignInRequest {

    @Email
    @NotBlank(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    @Pattern(
        regexp = Const.PASSWORD_PATTERN,
        message = "비밀번호 형식이 올바르지 않습니다."
    )
    private String password;

    @NotBlank(message = "유저 타입 정보는 필수입니다.")
    private String userType;

    @Builder
    private SignInRequest(String email, String password, String userType) {
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

}
