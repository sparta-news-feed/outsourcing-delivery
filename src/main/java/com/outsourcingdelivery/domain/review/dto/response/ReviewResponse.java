package com.outsourcingdelivery.domain.review.dto.response;

import com.outsourcingdelivery.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponse {

    private final Long reviewId;

    private final String contents;

    private final Short rating;

    private final String username;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    @Builder
    private ReviewResponse(Long reviewId, String contents, Short rating, String username, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.reviewId = reviewId;
        this.contents = contents;
        this.rating = rating;
        this.username = username;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static ReviewResponse toDto(Review review) {
        return ReviewResponse.builder()
            .reviewId(review.getReviewId())
            .contents(review.getContents())
            .rating(review.getRating())
            .username(review.getUser().getUsername())
            .createdAt(review.getCreatedAt())
            .modifiedAt(review.getModifiedAt())
            .build();
    }

}
