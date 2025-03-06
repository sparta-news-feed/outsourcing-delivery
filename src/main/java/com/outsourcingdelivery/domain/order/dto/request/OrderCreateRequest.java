package com.outsourcingdelivery.domain.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {
    @NotNull(message = "가게 ID는 필수값입니다.")
    private Long storeId;
    @NotNull(message = "메뉴 ID는 필수값입니다.")
    private Long menuId;
    @NotNull(message = "주문 수량은 필수값입니다.")
    @Min(1)
    private Integer amount;

    @Builder
    private OrderCreateRequest(Long storeId, Long menuId, Integer amount) {
        this.storeId = storeId;
        this.menuId = menuId;
        this.amount = amount;
    }
}
