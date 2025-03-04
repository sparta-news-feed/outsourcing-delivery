package com.outsourcingdelivery.domain.menu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MenuSaveRequest {
    @NotNull(message = "메뉴 이름 입력은 필수입니다.")
    private String menuName;

    @NotNull(message = "메뉴 가격 입력은 필수입니다..")
    private Integer price;

    private String description;

    @Builder
    private MenuSaveRequest(String menuName, Integer price, String description) {
        this.menuName = menuName;
        this.price = price;
        this.description = description;
    }
}