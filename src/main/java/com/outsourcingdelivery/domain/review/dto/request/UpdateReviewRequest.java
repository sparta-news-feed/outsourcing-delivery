package com.outsourcingdelivery.domain.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UpdateReviewRequest {

    @NotBlank(message = "리뷰 내용 작성은 필수입니다.")
    private String contents;

    @NotNull(message = "평점은 필수입니다.")
    @Min(1)
    @Max(5)
    private Short rating;

    @Builder
    private UpdateReviewRequest(String contents, Short rating) {
        this.contents = contents;
        this.rating = rating;
    }
}
