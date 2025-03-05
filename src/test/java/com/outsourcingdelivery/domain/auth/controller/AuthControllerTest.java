package com.outsourcingdelivery.domain.auth.controller;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.ControllerTestSupport;
import com.outsourcingdelivery.domain.auth.dto.request.SignInRequest;
import com.outsourcingdelivery.domain.auth.dto.request.SignUpRequest;
import com.outsourcingdelivery.domain.auth.dto.request.WithDrawRequest;
import com.outsourcingdelivery.domain.auth.dto.response.AccessTokenResponse;
import com.outsourcingdelivery.domain.auth.dto.response.TokenResponse;
import com.outsourcingdelivery.domain.user.enums.UserType;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends ControllerTestSupport {

    /**
     * ServiceTest 에서 모든 기능 검사를 매우 빡시게 했기때문에
     * Controller 에선 숨좀 돌릴겸 가볍게 해당 HttpStatus Code 가 제대로 넘어오는지만 검사하면 됩니다!
     */
    @DisplayName("회원가입 - 성공")
    @Test
    void signup1() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .username("홍길동")
            .userType(UserType.OWNER)
            .phoneNumber("01012345678")
            .address("서울")
            .build();

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("회원 가입에 성공했습니다."));
    }

    @DisplayName("회원가입 - 중복 이메일 (409 Conflict)")
    @Test
    void signup2() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .username("홍길동")
            .userType(UserType.OWNER)
            .phoneNumber("01012345678")
            .address("서울")
            .build();

        /**
         * 해당 코드가 Service 로직 내부에서 발생할 예외를 강제로 발생시킵니다.
         * 이를 통해 실제 비즈니스 로직을 실행하지 않고도, (대신 비즈니스 로직쪽은 ServiceTest 에서 꼼꼼히 검증하는게 좋겠죠?)
         * Controller 에서 해당 예외가 발생할 경우 올바른 상태 코드(409)가 반환되는지 테스트할 수 있습니다.
         * 그리고 예외로 설정해놓은 메세지를 매칭시켜서 올바르게 예외가 잘 발생하는지 테스트합니다.
         */
        doThrow(new ApplicationException(ErrorCode.DUPLICATE_EMAIL))
            .when(authService).signup(any(SignUpRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_EMAIL.getMessage()));
    }

    @DisplayName("회원가입 - 이미 탈퇴한 사용자 재가입 (401 UNAUTHORIZED)")
    @Test
    void signup3() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .username("홍길동")
            .userType(UserType.OWNER)
            .phoneNumber("01012345678")
            .address("서울")
            .build();

        doThrow(new ApplicationException(ErrorCode.DELETED_USER_CANNOT_REGISTER))
            .when(authService).signup(any(SignUpRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(ErrorCode.DELETED_USER_CANNOT_REGISTER.getMessage()));
    }

    @DisplayName("로그인 - 성공")
    @Test
    void login1() throws Exception {
        // given
        SignInRequest request = SignInRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .userType(UserType.OWNER)
            .build();

        ResponseCookie refreshToken = ResponseCookie.from("refreshToken", "refresh-token")
            .maxAge(0)
            .path("/")
            .build();

        TokenResponse response = new TokenResponse("Bearer jsonToken", refreshToken);

        when(authService.login(any(SignInRequest.class), any(LocalDateTime.class)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."))
            .andExpect(jsonPath("$.data.accessToken").value(containsString("Bearer ")));
    }

    @DisplayName("로그인 - 비밀번호 오류(401 UNAUTHORIZED)")
    @Test
    void login2() throws Exception {
        // given
        SignInRequest request = SignInRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .userType(UserType.OWNER)
            .build();

        doThrow(new ApplicationException(ErrorCode.INCORRECT_PASSWORD))
            .when(authService).login(any(SignInRequest.class), any(LocalDateTime.class));

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(ErrorCode.INCORRECT_PASSWORD.getMessage()));
    }

    @DisplayName("로그인 - 이미 탈퇴한 회원 로그인(401 UNAUTHORIZED)")
    @Test
    void login3() throws Exception {
        // given
        SignInRequest request = SignInRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .userType(UserType.OWNER)
            .build();

        doThrow(new ApplicationException(ErrorCode.ALREADY_DELETED_USER))
            .when(authService).login(any(SignInRequest.class), any(LocalDateTime.class));

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_DELETED_USER.getMessage()));
    }

    @DisplayName("로그인 - 존재하지 않는 유저(404 NOT_FOUND)")
    @Test
    void login4() throws Exception {
        // given
        SignInRequest request = SignInRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .userType(UserType.OWNER)
            .build();

        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_USER))
            .when(authService).login(any(SignInRequest.class), any(LocalDateTime.class));

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_USER.getMessage()));
    }

    @DisplayName("로그아웃 - 성공")
    @Test
    void logout1() throws Exception {
        // given
        ResponseCookie refreshToken = ResponseCookie.from("refreshToken", "")
            .maxAge(0)
            .path("/")
            .build();

        // when
        when(authService.logout(any(AuthUser.class)))
            .thenReturn(refreshToken);

        // then
        mockMvc.perform(post("/api/v1/auth/logout")
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(header().exists(SET_COOKIE))
            .andExpect(jsonPath("$.message").value("로그아웃에 성공했습니다."));
    }

    @DisplayName("로그아웃 - 존재하지 않는 유저(404 NOT_FOUND)")
    @Test
    void logout2() throws Exception {
        // given
        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_USER))
            .when(authService).logout(any(AuthUser.class));

        // when & then
        mockMvc.perform(post("/api/v1/auth/logout")
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_USER.getMessage())); // ✅ 에러 메시지 확인
    }

    @DisplayName("회원탈퇴 - 성공")
    @Test
    void withdraw1() throws Exception {
        // given
        ResponseCookie refreshToken = ResponseCookie.from("refreshToken", "")
            .maxAge(0)
            .path("/")
            .build();

        WithDrawRequest request = WithDrawRequest.builder()
            .password("Password1234!")
            .build();

        // when
        when(authService.withdraw(any(AuthUser.class), any(WithDrawRequest.class), any(LocalDateTime.class)))
            .thenReturn(refreshToken);

        // then
        mockMvc.perform(post("/api/v1/auth/withdraw")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(header().exists(SET_COOKIE))
            .andExpect(jsonPath("$.message").value("회원탈퇴에 성공했습니다."));
    }

    @DisplayName("회원탈퇴 - 비밀번호 오류(401 UNAUTHORIZED)")
    @Test
    void withdraw2() throws Exception {
        // given
        WithDrawRequest request = WithDrawRequest.builder()
            .password("Password1234!")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.INCORRECT_PASSWORD))
            .when(authService).withdraw(any(AuthUser.class), any(WithDrawRequest.class), any(LocalDateTime.class));

        // then
        mockMvc.perform(post("/api/v1/auth/withdraw")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(ErrorCode.INCORRECT_PASSWORD.getMessage()));
    }

    @DisplayName("회원탈퇴 - 이미 탈퇴한 회원(401 UNAUTHORIZED)")
    @Test
    void withdraw3() throws Exception {
        // given
        WithDrawRequest request = WithDrawRequest.builder()
            .password("Password1234!")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.ALREADY_DELETED_USER))
            .when(authService).withdraw(any(AuthUser.class), any(WithDrawRequest.class), any(LocalDateTime.class));

        // then
        mockMvc.perform(post("/api/v1/auth/withdraw")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_DELETED_USER.getMessage()));
    }

    @DisplayName("회원탈퇴 - 존재하지 않는 유저(404 NOT_FOUND)")
    @Test
    void withdraw4() throws Exception {
        // given
        WithDrawRequest request = WithDrawRequest.builder()
            .password("Password1234!")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_USER))
            .when(authService).withdraw(any(AuthUser.class), any(WithDrawRequest.class), any(LocalDateTime.class));

        // then
        mockMvc.perform(post("/api/v1/auth/withdraw")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_USER.getMessage()));
    }

    @DisplayName("액세스 토큰 재발급 - 성공")
    @Test
    void refresh1() throws Exception {
        // given
        String refreshTokenValue = "valid-refresh-token";
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse("Bearer new-access-token");

        // when
        when(authService.refresh(refreshTokenValue))
            .thenReturn(accessTokenResponse);

        // then
        mockMvc.perform(post("/api/v1/auth//refresh")
                .contentType(APPLICATION_JSON)
                .cookie(new Cookie("refreshToken", refreshTokenValue)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("토큰이 성공적으로 재발급 되었습니다."))
            .andExpect(jsonPath("$.data.accessToken").value(containsString("Bearer ")));
    }

    @DisplayName("액세스 토큰 재발급 - 리프레시 토큰 만료(401 UNAUTHORIZED)")
    @Test
    void refresh2() throws Exception {
        // given
        String refreshTokenValue = "valid-refresh-token";

        // when
        doThrow(new ApplicationException(ErrorCode.EXPIRED_REFRESH_TOKEN))
            .when(authService).refresh(anyString());

        // then
        mockMvc.perform(post("/api/v1/auth//refresh")
                .contentType(APPLICATION_JSON)
                .cookie(new Cookie("refreshToken", refreshTokenValue)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(ErrorCode.EXPIRED_REFRESH_TOKEN.getMessage()));
    }

    @DisplayName("액세스 토큰 재발급 - 리프레시 토큰 서버에서 찾지 못함(404 NOT_FOUND)")
    @Test
    void refresh3() throws Exception {
        // given
        String refreshTokenValue = "valid-refresh-token";

        // when
        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_TOKEN))
            .when(authService).refresh(anyString());

        // then
        mockMvc.perform(post("/api/v1/auth//refresh")
                .contentType(APPLICATION_JSON)
                .cookie(new Cookie("refreshToken", refreshTokenValue)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_TOKEN.getMessage()));
    }
}