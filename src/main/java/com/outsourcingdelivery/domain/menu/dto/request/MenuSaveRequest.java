package com.outsourcingdelivery.domain.menu.dto.request;

import lombok.Getter;

@Getter
public class MenuSaveRequest {

    private String menuName;
    private Integer price;
    private String description;

}