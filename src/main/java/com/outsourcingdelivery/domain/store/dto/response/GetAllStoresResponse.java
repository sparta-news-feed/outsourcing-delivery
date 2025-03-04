package com.outsourcingdelivery.domain.store.dto.response;

import com.outsourcingdelivery.domain.store.enums.StoreStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetAllStoresResponse {
    private final Long storeId;
    private final String storeName;
    private final Integer minOrderPrice;
    private final StoreStatus storeStatus;
    private final Long reviewCount;
}
