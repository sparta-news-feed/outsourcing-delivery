package com.outsourcingdelivery.domain.review.controller;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.ControllerTestSupport;
import com.outsourcingdelivery.domain.review.dto.request.CreateReviewRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewControllerTest extends ControllerTestSupport {

    @DisplayName("리뷰 생성하기 - 성공")
    @Test
    void createReview1() throws Exception {
        // given
        CreateReviewRequest request = CreateReviewRequest.builder()
            .contents("리뷰")
            .rating((short) 5)
            .storeId(1L)
            .orderNo(11111111L)
            .build();

        // when & then
        mockMvc.perform(post("/api/v1/reviews")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("리뷰 생성에 성공했습니다."));

    }

    @DisplayName("리뷰 생성하기 - 존재하지 않는 유저(404 NOT_FOUND)")
    @Test
    void createReview2() throws Exception {
        // given
        CreateReviewRequest request = CreateReviewRequest.builder()
            .contents("리뷰")
            .rating((short) 5)
            .storeId(1L)
            .orderNo(11111111L)
            .build();

        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_USER))
            .when(reviewService).createReview(any(AuthUser.class), any(CreateReviewRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/reviews")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_USER.getMessage()));

    }

    @DisplayName("리뷰 생성하기 - 존재하지 않는 가게(404 NOT_FOUND)")
    @Test
    void createReview3() throws Exception {
        // given
        CreateReviewRequest request = CreateReviewRequest.builder()
            .contents("리뷰")
            .rating((short) 5)
            .storeId(1L)
            .orderNo(11111111L)
            .build();

        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_STORE))
            .when(reviewService).createReview(any(AuthUser.class), any(CreateReviewRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/reviews")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_STORE.getMessage()));

    }

    @DisplayName("리뷰 생성하기 - 존재하지 않는 주문(404 NOT_FOUND)")
    @Test
    void createReview4() throws Exception {
        // given
        CreateReviewRequest request = CreateReviewRequest.builder()
            .contents("리뷰")
            .rating((short) 5)
            .storeId(1L)
            .orderNo(11111111L)
            .build();

        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_ORDER))
            .when(reviewService).createReview(any(AuthUser.class), any(CreateReviewRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/reviews")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_ORDER.getMessage()));

    }

    @DisplayName("리뷰 생성하기 - 배달이 완료되지 않았을때 리뷰 작성시 오류 발생(403 - FORBIDDEN)")
    @Test
    void createReview5() throws Exception {
        // given
        CreateReviewRequest request = CreateReviewRequest.builder()
            .contents("리뷰")
            .rating((short) 5)
            .storeId(1L)
            .orderNo(11111111L)
            .build();

        doThrow(new ApplicationException(ErrorCode.REVIEW_CREATION_FORBIDDEN))
            .when(reviewService).createReview(any(AuthUser.class), any(CreateReviewRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/reviews")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value(ErrorCode.REVIEW_CREATION_FORBIDDEN.getMessage()));

    }



}