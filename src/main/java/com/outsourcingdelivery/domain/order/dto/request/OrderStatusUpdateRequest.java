package com.outsourcingdelivery.domain.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderStatusUpdateRequest {
    @NotNull(message = "가게 ID는 필수값입니다.")
    private Long storeId;
    @NotNull(message = "주문번호는 필수값입니다.")
    private Long orderNo;
    @NotBlank(message = "변경할 주문상태는 필수값입니다.")
    private String orderStatus;
}
