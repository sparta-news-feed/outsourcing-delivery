package com.outsourcingdelivery.domain.menu.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MenuSaveRequest {
    @NotNull(message = "메뉴 이름 입력은 필수입니다.")
    @Size(max = 25, message = "메뉴 이름은 최대 25자까지 입력할 수 있습니다.")
    private String menuName;

    @NotNull(message = "메뉴 가격 입력은 필수입니다.")
    private Integer price;

    @Size(max = 100, message = "설명은 최대 100자까지 입력할 수 있습니다.")
    private String description;

    @Builder
    private MenuSaveRequest(String menuName, Integer price, String description) {
        this.menuName = menuName;
        this.price = price;
        this.description = description;
    }
}