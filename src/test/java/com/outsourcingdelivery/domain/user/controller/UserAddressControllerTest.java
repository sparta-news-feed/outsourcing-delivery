package com.outsourcingdelivery.domain.user.controller;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.ControllerTestSupport;
import com.outsourcingdelivery.domain.user.dto.request.CreateUserAddressRequest;
import com.outsourcingdelivery.domain.user.dto.request.UpdateUserAddressRequest;
import com.outsourcingdelivery.domain.user.dto.response.UserAddressResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAddressControllerTest extends ControllerTestSupport {

    @DisplayName("유저 주소지 생성 - 성공")
    @Test
    void createUserAddress1() throws Exception {
        // given
        CreateUserAddressRequest request = CreateUserAddressRequest.builder()
            .address("서울")
            .build();

        // when
        when(userAddressService.createUserAddress(any(AuthUser.class), any(CreateUserAddressRequest.class)))
            .thenReturn(1L);

        // then
        mockMvc.perform(post("/api/v1/users/address")
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("새로운 주소 생성에 성공했습니다."));
    }

    @DisplayName("유저 주소지 생성 - 최대 10개의 주소만 등록가능 10개 초과시 오류(400 - BAD_REQUEST)")
    @Test
    void createUserAddress2() throws Exception {
        // given
        CreateUserAddressRequest request = CreateUserAddressRequest.builder()
            .address("서울")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.MAX_USER_ADDRESS_LIMIT_EXCEEDED))
            .when(userAddressService).createUserAddress(any(AuthUser.class), any(CreateUserAddressRequest.class));

        // then
        mockMvc.perform(post("/api/v1/users/address")
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ErrorCode.MAX_USER_ADDRESS_LIMIT_EXCEEDED.getMessage()));
    }

    @DisplayName("유저 주소지 생성 - 존재하지 않는 유저(404 - NOT_FOUND)")
    @Test
    void createUserAddress3() throws Exception {
        // given
        CreateUserAddressRequest request = CreateUserAddressRequest.builder()
            .address("서울")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_USER))
            .when(userAddressService).createUserAddress(any(AuthUser.class), any(CreateUserAddressRequest.class));

        // then
        mockMvc.perform(post("/api/v1/users/address")
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_USER.getMessage()));
    }

    @DisplayName("유저 주소 목록 조회 - 성공")
    @Test
    void getAllUserAddress1() throws Exception {
        // given
        List<UserAddressResponse> response = List.of(
            UserAddressResponse.builder()
                .userAddressId(1L)
                .address("서울")
                .build(),
            UserAddressResponse.builder()
                .userAddressId(2L)
                .address("부산")
                .build()
        );

        // when
        when(userAddressService.getAllUserAddress(any(AuthUser.class)))
            .thenReturn(response);

        // then
        mockMvc.perform(get("/api/v1/users/address")
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].userAddressId").value(1L))
            .andExpect(jsonPath("$.data[0].address").value("서울"))
            .andExpect(jsonPath("$.data[1].userAddressId").value(2L))
            .andExpect(jsonPath("$.data[1].address").value("부산"));
    }

    @DisplayName("유저 주소지 업데이트 - 성공")
    @Test
    void updateUserAddress1() throws Exception {
        // given
        Long addressId = 1L;

        UpdateUserAddressRequest request = UpdateUserAddressRequest.builder()
            .address("서울")
            .build();

        // when
        doNothing()
            .when(userAddressService)
                .updateUserAddress(
                    any(AuthUser.class),
                    anyLong(),
                    any(UpdateUserAddressRequest.class)
                );

        // then
        mockMvc.perform(put("/api/v1/users/address/{addressId}", addressId)
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("주소 변경에 성공했습니다."));
    }

    @DisplayName("유저 주소지 업데이트 - 다른 사람의 주소 변경 불가(403 - FORBIDDEN)")
    @Test
    void updateUserAddress2() throws Exception {
        // given
        Long addressId = 1L;

        UpdateUserAddressRequest request = UpdateUserAddressRequest.builder()
            .address("서울")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.FORBIDDEN_ADDRESS_UPDATE))
            .when(userAddressService)
            .updateUserAddress(
                any(AuthUser.class),
                anyLong(),
                any(UpdateUserAddressRequest.class)
            );

        // then
        mockMvc.perform(put("/api/v1/users/address/{addressId}", addressId)
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value(ErrorCode.FORBIDDEN_ADDRESS_UPDATE.getMessage()));
    }

    @DisplayName("유저 주소지 업데이트 - 존재하지 않는 유저 주소지 오류(404 - NOT_FOUND)")
    @Test
    void updateUserAddress3() throws Exception {
        // given
        Long addressId = 1L;

        UpdateUserAddressRequest request = UpdateUserAddressRequest.builder()
            .address("서울")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_USER_ADDRESS))
            .when(userAddressService)
            .updateUserAddress(
                any(AuthUser.class),
                anyLong(),
                any(UpdateUserAddressRequest.class)
            );

        // then
        mockMvc.perform(put("/api/v1/users/address/{addressId}", addressId)
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_USER_ADDRESS.getMessage()));
    }

    @DisplayName("유저 주소지 단건 삭제 - 성공")
    @Test
    void deleteUserAddress1() throws Exception {
        // given
        Long addressId = 1L;

        // when
        doNothing()
            .when(userAddressService)
            .deleteUserAddress(any(AuthUser.class), anyLong());


        // then
        mockMvc.perform(delete("/api/v1/users/address/{addressId}", addressId)
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("주소 삭제에 성공했습니다."));
    }

    @DisplayName("유저 주소지 단건 삭제 - 기본 주소지는 삭제 불가(400 - BAD_REQUEST)")
    @Test
    void deleteUserAddress2() throws Exception {
        // given
        Long addressId = 1L;

        // when
        doThrow(new ApplicationException(ErrorCode.CANNOT_DELETE_PRIMARY_ADDRESS))
            .when(userAddressService)
            .deleteUserAddress(any(AuthUser.class), anyLong());

        // then
        mockMvc.perform(delete("/api/v1/users/address/{addressId}", addressId)
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ErrorCode.CANNOT_DELETE_PRIMARY_ADDRESS.getMessage()));
    }

    @DisplayName("유저 주소지 단건 삭제 - 자신의 주소가 아닌것을 삭제할때 오류(403 - FORBIDDEN)")
    @Test
    void deleteUserAddress3() throws Exception {
        // given
        Long addressId = 1L;

        // when
        doThrow(new ApplicationException(ErrorCode.FORBIDDEN_ADDRESS_DELETE))
            .when(userAddressService)
            .deleteUserAddress(any(AuthUser.class), anyLong());

        // then
        mockMvc.perform(delete("/api/v1/users/address/{addressId}", addressId)
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value(ErrorCode.FORBIDDEN_ADDRESS_DELETE.getMessage()));
    }

    @DisplayName("유저 주소지 단건 삭제 - 일치하는 유저 주소지 아이디가 없음(404 - NOT_FOUND)")
    @Test
    void deleteUserAddress4() throws Exception {
        // given
        Long addressId = 1L;

        // when
        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_USER_ADDRESS))
            .when(userAddressService)
            .deleteUserAddress(any(AuthUser.class), anyLong());

        // then
        mockMvc.perform(delete("/api/v1/users/address/{addressId}", addressId)
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_USER_ADDRESS.getMessage()));
    }

}