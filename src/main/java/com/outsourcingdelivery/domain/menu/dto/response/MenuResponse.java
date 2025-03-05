package com.outsourcingdelivery.domain.menu.dto.response;

import lombok.Getter;

@Getter
public class MenuResponse {
    private final Long menuId;
    private final String menuName;
    private final Integer price;
    private final String description;

    public MenuResponse(Long menuId, String menuName, Integer price, String description) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.price = price;
        this.description = description;
    }
}
