package com.outsourcingdelivery.domain.review.controller;

import com.outsourcingdelivery.common.auth.Auth;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.domain.review.dto.request.CreateReviewRequest;
import com.outsourcingdelivery.domain.review.dto.request.UpdateReviewRequest;
import com.outsourcingdelivery.domain.review.dto.response.ReviewResponse;
import com.outsourcingdelivery.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<String>> createReview(
        @Auth AuthUser authUser,
        @Valid @RequestBody CreateReviewRequest request
    ) {
        reviewService.createReview(authUser, request);
        return ResponseEntity.ok(ApiResponse.success("리뷰 생성에 성공했습니다."));
    }

    @GetMapping("/stores/{storeId}/reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getAllReviews(
        @PathVariable(name = "storeId") Long storeId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Integer ratingStart,
        @RequestParam(required = false) Integer ratingEnd
    ) {
        PageResponse<ReviewResponse> response = reviewService.getAllReviews(
            storeId, page, size, ratingStart, ratingEnd
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<String>> updateReview(
        @Auth AuthUser authUser,
        @PathVariable("reviewId") Long reviewId,
        @Valid @RequestBody UpdateReviewRequest request
    ) {
        LocalDateTime now = LocalDateTime.now();
        reviewService.updateReview(authUser, reviewId, request, now);
        return ResponseEntity.ok(ApiResponse.success("리뷰 수정에 성공했습니다."));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(
        @Auth AuthUser authUser,
        @PathVariable("reviewId") Long reviewId
    ) {
        reviewService.deleteReview(authUser, reviewId);
        return ResponseEntity.ok(ApiResponse.success("리뷰 삭제에 성공했습니다."));
    }
}
