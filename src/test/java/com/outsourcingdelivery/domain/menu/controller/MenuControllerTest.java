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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
