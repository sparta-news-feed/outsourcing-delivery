package com.outsourcingdelivery.domain.store.dto.request;

import com.outsourcingdelivery.common.Const;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CreateStoreRequest {
    @NotNull(message = "가게이름 입력은 필수입니다.")
    private String storeName;

    @NotNull(message = "최소 주문 금액 입력은 필수입니다.")
    private Integer minOrderPrice;

    @NotNull(message = "전화번호 입력은 필수입니다.")
    @Pattern(
            regexp = Const.PHONE_NUMBER_PATTERN,
            message = "전화번호 형식이 올바르지 않습니다."
    )
    private String phoneNumber;

    @NotNull(message = "가게 주소 입력은 필수입니다.")
    private String address;
}
