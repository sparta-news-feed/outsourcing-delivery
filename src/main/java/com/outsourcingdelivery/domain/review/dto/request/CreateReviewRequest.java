package com.outsourcingdelivery.domain.review.dto.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CreateReviewRequest {

    @NotBlank(message = "리뷰 내용 작성은 필수입니다.")
    private String contents;

    @NotNull(message = "평점은 필수입니다.")
//    @Size(min = 1, max = 5)   // Number 타입은 예외발생
    @Min(1)
    @Max(5)
    private Short rating;

    @NotNull(message = "Order Id는 필수입니다.")
    private Long orderId;

    @NotNull(message = "Store Id는 필수입니다.")
    private Long storeId;

    @Builder
    private CreateReviewRequest(String contents, Short rating, Long orderId, Long storeId) {
        this.contents = contents;
        this.rating = rating;
        this.orderId = orderId;
        this.storeId = storeId;
    }
}
