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
    @Min(1)
    @Max(5)
    private Short rating;

    @NotNull(message = "주문번호(orderNo)는 필수입니다.")
    private Long orderNo;

    @NotNull(message = "Store Id는 필수입니다.")
    private Long storeId;

    @Builder
    private CreateReviewRequest(String contents, Short rating, Long orderNo, Long storeId) {
        this.contents = contents;
        this.rating = rating;
        this.orderNo = orderNo;
        this.storeId = storeId;
    }
}
