package com.outsourcingdelivery.domain.review.controller;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.ControllerTestSupport;
import com.outsourcingdelivery.domain.review.dto.request.CreateReviewRequest;
import com.outsourcingdelivery.domain.review.dto.request.UpdateReviewRequest;
import com.outsourcingdelivery.domain.review.dto.response.ReviewResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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


    @DisplayName("가게 리뷰 전체 조회 - 성공")
    @Test
    void getAllReviews1() throws Exception {
        // given
        Long storeId = 1L;

        PageResponse<ReviewResponse> response = PageResponse.<ReviewResponse>builder()
            .content(new ArrayList<>())
            .size(10)
            .number(1)
            .totalElements(0)
            .totalPages(0)
            .build();

        when(reviewService.getAllReviews(storeId, 1, 10, null, null))
            .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/stores/{storeId}/reviews", storeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."))
            .andExpect(jsonPath("$.data.content").isArray());
    }

    @DisplayName("리뷰 업데이트 - 성공")
    @Test
    void updateReview1() throws Exception {
        // given
        Long reviewId = 1L;

        UpdateReviewRequest request = UpdateReviewRequest.builder()
            .contents("수정")
            .rating((short) 5)
            .build();

        // when & then
        mockMvc.perform(put("/api/v1/reviews/{reviewId}", reviewId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("리뷰 수정에 성공했습니다."));

    }

    @DisplayName("리뷰 업데이트 - 다른 유저가 업데이트 시도시 오류 발생(403 - FORBIDDEN)")
    @Test
    void updateReview2() throws Exception {
        // given
        Long reviewId = 1L;

        UpdateReviewRequest request = UpdateReviewRequest.builder()
            .contents("수정")
            .rating((short) 5)
            .build();

        doThrow(new ApplicationException(ErrorCode.REVIEW_EDIT_FORBIDDEN))
            .when(reviewService)
            .updateReview(
                any(AuthUser.class),
                anyLong(),
                any(UpdateReviewRequest.class),
                any(LocalDateTime.class)
            );

        // when & then
        mockMvc.perform(put("/api/v1/reviews/{reviewId}", reviewId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value(ErrorCode.REVIEW_EDIT_FORBIDDEN.getMessage()));

    }

    @DisplayName("리뷰 업데이트 - 리뷰 작성 후 3일이 지나고 수정하면 오류 발생(403 - FORBIDDEN)")
    @Test
    void updateReview3() throws Exception {
        // given
        Long reviewId = 1L;

        UpdateReviewRequest request = UpdateReviewRequest.builder()
            .contents("수정")
            .rating((short) 5)
            .build();

        doThrow(new ApplicationException(ErrorCode.REVIEW_EDIT_EXPIRED))
            .when(reviewService)
            .updateReview(
                any(AuthUser.class),
                anyLong(),
                any(UpdateReviewRequest.class),
                any(LocalDateTime.class)
            );

        // when & then
        mockMvc.perform(put("/api/v1/reviews/{reviewId}", reviewId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value(ErrorCode.REVIEW_EDIT_EXPIRED.getMessage()));

    }

    @DisplayName("리뷰 업데이트 - 존재하지 않는 리뷰(404 - NOT_FOUND)")
    @Test
    void updateReview4() throws Exception {
        // given
        Long reviewId = 1L;

        UpdateReviewRequest request = UpdateReviewRequest.builder()
            .contents("수정")
            .rating((short) 5)
            .build();

        doThrow(new ApplicationException(ErrorCode.REVIEW_NOT_FOUND))
            .when(reviewService)
            .updateReview(
                any(AuthUser.class),
                anyLong(),
                any(UpdateReviewRequest.class),
                any(LocalDateTime.class)
            );

        // when & then
        mockMvc.perform(put("/api/v1/reviews/{reviewId}", reviewId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.REVIEW_NOT_FOUND.getMessage()));

    }

    @DisplayName("리뷰 단건 삭제 - 정상")
    @Test
    void deleteReview1() throws Exception {
        // given
        Long reviewId = 1L;

        // when & then
        mockMvc.perform(delete("/api/v1/reviews/{reviewId}", reviewId)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("리뷰 삭제에 성공했습니다."));

    }

    @DisplayName("리뷰 단건 삭제 - 자신의 리뷰가 아닌 것을 삭제 요청하면 오류 발생(403 - FORBIDDEN)")
    @Test
    void deleteReview2() throws Exception {
        // given
        Long reviewId = 1L;

        // when
        doThrow(new ApplicationException(ErrorCode.REVIEW_EDIT_FORBIDDEN))
            .when(reviewService)
            .deleteReview(any(AuthUser.class), anyLong());

        // then
        mockMvc.perform(delete("/api/v1/reviews/{reviewId}", reviewId)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value(ErrorCode.REVIEW_EDIT_FORBIDDEN.getMessage()));

    }

    @DisplayName("리뷰 단건 삭제 - 존재하지 않는 리뷰 오류 발생(404 - NOT_FOUND)")
    @Test
    void deleteReview3() throws Exception {
        // given
        Long reviewId = 1L;

        // when
        doThrow(new ApplicationException(ErrorCode.REVIEW_NOT_FOUND))
            .when(reviewService)
            .deleteReview(any(AuthUser.class), anyLong());

        // then
        mockMvc.perform(delete("/api/v1/reviews/{reviewId}", reviewId)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.REVIEW_NOT_FOUND.getMessage()));

    }
}