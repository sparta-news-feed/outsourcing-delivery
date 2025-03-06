package com.outsourcingdelivery.domain.order.dto.request;

import com.outsourcingdelivery.domain.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderStatusUpdateRequest {
    @NotNull(message = "가게 ID는 필수값입니다.")
    private Long storeId;
    @NotNull(message = "주문번호는 필수값입니다.")
    private Long orderNo;
    @NotNull(message = "변경할 주문 상태 값은 필수입니다.")
    private OrderStatus orderStatus;

    @Builder
    private OrderStatusUpdateRequest(Long storeId, Long orderNo, OrderStatus orderStatus) {
        this.storeId = storeId;
        this.orderNo = orderNo;
        this.orderStatus = orderStatus;
    }
}
