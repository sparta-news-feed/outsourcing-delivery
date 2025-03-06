package com.outsourcingdelivery.domain.menu.controller;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.domain.ControllerTestSupport;
import com.outsourcingdelivery.domain.menu.dto.request.MenuSaveRequest;
import com.outsourcingdelivery.domain.user.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MenuControllerTest extends ControllerTestSupport {

    @DisplayName("메뉴 생성 - 성공")
    @Test
    void saveMenu1() throws Exception {
        // given
        Long storeId = 1L;
        AuthUser authUser = AuthUser.builder()
                .userId(1L)
                .userType(UserType.USER)
                .build();
        MenuSaveRequest request = MenuSaveRequest.builder()
                .menuName("메뉴1")
                .price(10000)
                .description("설명1")
                .build();

        doNothing().when(menuService).createMenu(any(AuthUser.class), any(Long.class), any(MenuSaveRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/stores/{storeId}/menus", storeId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                                .header(AUTHORIZATION, accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("메뉴 생성에 성공했습니다."));
    }

    @DisplayName("메뉴 생성 - 유효하지 않은 입력값으로 실패")
    @Test
    void saveMenu_invalidInput_failure() throws Exception {
        // given
        Long storeId = 1L;
        MenuSaveRequest request = MenuSaveRequest.builder()
                .menuName("")
                .price(-1000)
                .description("설명1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/stores/{storeId}/menus", storeId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(AUTHORIZATION, accessToken))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 수정 - 성공")
    @Test
    void updateMenu_success() throws Exception {
        // given
        Long storeId = 1L;
        Long menuId = 1L;
        AuthUser authUser = AuthUser.builder()
                .userId(1L)
                .userType(UserType.USER)
                .build();
        MenuSaveRequest request = MenuSaveRequest.builder()
                .menuName("수정된 메뉴")
                .price(20000)
                .description("수정된 설명")
                .build();

        doNothing().when(menuService).updateMenu(any(AuthUser.class), any(Long.class), any(Long.class), any(MenuSaveRequest.class));

        // when & then
        mockMvc.perform(put("/api/v1/stores/{storeId}/menus/{menuId}", storeId, menuId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("메뉴 수정에 성공했습니다."));
    }

    @DisplayName("메뉴 수정 - 유효하지 않은 입력값으로 실패")
    @Test
    void updateMenu_invalidInput_failure() throws Exception {
        // given
        Long storeId = 1L;
        Long menuId = 1L;
        MenuSaveRequest request = MenuSaveRequest.builder()
                .menuName("")
                .price(-20000)
                .description("수정된 설명")
                .build();

        // when & then
        mockMvc.perform(put("/api/v1/stores/{storeId}/menus/{menuId}", storeId, menuId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(AUTHORIZATION, accessToken))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 삭제 - 성공")
    @Test
    void deleteMenu_success() throws Exception {
        // given
        Long storeId = 1L;
        Long menuId = 1L;
        AuthUser authUser = AuthUser.builder()
                .userId(1L)
                .userType(UserType.USER)
                .build();

        doNothing().when(menuService).deleteMenu(any(AuthUser.class), any(Long.class), any(Long.class));

        // when & then
        mockMvc.perform(delete("/api/v1/stores/{storeId}/menus/{menuId}", storeId, menuId)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("메뉴 삭제에 성공했습니다."));

    }
}
