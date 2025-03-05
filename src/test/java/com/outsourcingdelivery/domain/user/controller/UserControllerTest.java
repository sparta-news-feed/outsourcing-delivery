package com.outsourcingdelivery.domain.user.controller;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.ControllerTestSupport;
import com.outsourcingdelivery.domain.user.dto.request.UpdatePasswordRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupport {

    @DisplayName("비밀번호 변경 - 성공")
    @Test
    void updatePassword1() throws Exception {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password1234!")
            .newPassword("Password12345!")
            .build();

        // when
        doNothing().
            when(userService).updatePassword(any(AuthUser.class), any(UpdatePasswordRequest.class));

        // then
        mockMvc.perform(put("/api/v1/users/password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("비밀번호 변경에 성공했습니다."));
    }

    @DisplayName("비밀번호 변경 - 비밀번호가 일치하지 않음 오류(401 - UNAUTHORIZED)")
    @Test
    void updatePassword2() throws Exception {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password1234!")
            .newPassword("Password12345!")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.INCORRECT_PASSWORD))
            .when(userService).updatePassword(any(AuthUser.class), any(UpdatePasswordRequest.class));

        // then
        mockMvc.perform(put("/api/v1/users/password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(ErrorCode.INCORRECT_PASSWORD.getMessage()));
    }

    @DisplayName("비밀번호 변경 - 기존 비밀번호와 새 비밀번호가 같을 때 오류(409 - CONFLICT)")
    @Test
    void updatePassword3() throws Exception {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password1234!")
            .newPassword("Password12345!")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.SAME_AS_OLD_PASSWORD))
            .when(userService).updatePassword(any(AuthUser.class), any(UpdatePasswordRequest.class));

        // then
        mockMvc.perform(put("/api/v1/users/password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(ErrorCode.SAME_AS_OLD_PASSWORD.getMessage()));
    }

    @DisplayName("기본 주소지 변경 - 성공")
    @Test
    void updatePrimaryAddress1() throws Exception {
        // given
        Long addressId = 1L;

        // when
        doNothing()
            .when(userService).updatePrimaryAddress(any(AuthUser.class), anyLong());

        // then
        mockMvc.perform(put("/api/v1/users/primary-address/{addressId}", addressId)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("기본 주소지 변경에 성공했습니다."));
    }

    @DisplayName("기본 주소지 변경 - 존재하지 않는 유저(404 - NOT_FOUND)")
    @Test
    void updatePrimaryAddress2() throws Exception {
        // given
        Long addressId = 1L;

        // when
        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_USER))
            .when(userService).updatePrimaryAddress(any(AuthUser.class), anyLong());

        // then
        mockMvc.perform(put("/api/v1/users/primary-address/{addressId}", addressId)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_USER.getMessage()));
    }

    @DisplayName("기본 주소지 변경 - 존재하지 않는 유저 주소 데이터(404 - NOT_FOUND)")
    @Test
    void updatePrimaryAddress3() throws Exception {
        // given
        Long addressId = 1L;

        // when
        doThrow(new ApplicationException(ErrorCode.USER_ADDRESS_NOT_FOUND))
            .when(userService).updatePrimaryAddress(any(AuthUser.class), anyLong());

        // then
        mockMvc.perform(put("/api/v1/users/primary-address/{addressId}", addressId)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.USER_ADDRESS_NOT_FOUND.getMessage()));
    }

    @DisplayName("기본 주소지 변경 - 이미 기본 주소지를 또 등록하려할때 발생하는 오류(409 - CONFLICT)")
    @Test
    void updatePrimaryAddress4() throws Exception {
        // given
        Long addressId = 1L;

        // when
        doThrow(new ApplicationException(ErrorCode.PRIMARY_ADDRESS_ALREADY_SET))
            .when(userService).updatePrimaryAddress(any(AuthUser.class), anyLong());

        // then
        mockMvc.perform(put("/api/v1/users/primary-address/{addressId}", addressId)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(ErrorCode.PRIMARY_ADDRESS_ALREADY_SET.getMessage()));
    }
}