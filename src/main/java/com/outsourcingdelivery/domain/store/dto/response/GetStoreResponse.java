package com.outsourcingdelivery.domain.store.dto.response;

import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.storeSchedule.dto.Response.StoreScheduleResponse;
import com.outsourcingdelivery.domain.store.enums.StoreStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class GetStoreResponse {
    private final Long storeId;
    private final String storeName;
    private final Integer minOrderPrice;
    private final StoreStatus storeStatus;
    private final String address;
    private final Long reviewCount;
    private final List<StoreScheduleResponse> storeSchedules;

    public GetStoreResponse(Store store, List<StoreScheduleResponse> storeSchedules) {
        this.storeId = store.getStoreId();
        this.storeName = store.getStoreName();
        this.minOrderPrice = store.getMinOrderPrice();
        this.storeStatus = store.getStoreStatus();
        this.address = store.getAddress();
        this.reviewCount = store.getReviewCount();
        this.storeSchedules = storeSchedules;
    }
}
