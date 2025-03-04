package com.outsourcingdelivery.domain.store.dto.request;

import lombok.Getter;

@Getter
public class UpdateStoreRequest {
    private String storeName;
    private Integer minOrderPrice;
    private String phoneNumber;
    private String address;
}
