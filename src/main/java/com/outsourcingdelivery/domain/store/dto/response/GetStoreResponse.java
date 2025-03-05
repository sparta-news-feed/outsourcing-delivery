package com.outsourcingdelivery.domain.store.dto.response;

import com.outsourcingdelivery.domain.menu.dto.response.MenuResponse;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.storeSchedule.dto.Response.StoreScheduleResponse;
import com.outsourcingdelivery.domain.store.enums.StoreStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class GetStoreResponse {
    private final Long storeId;
    private final String username;
    private final String storeName;
    private final String phoneNumber;
    private final Integer minOrderPrice;
    private final StoreStatus storeStatus;
    private final String address;
    private final Long reviewCount;
    private final List<StoreScheduleResponse> storeSchedules;
    private final List<MenuResponse> menus;

    public GetStoreResponse(Store store, List<StoreScheduleResponse> storeSchedules, List<MenuResponse> menus) {
        this.storeId = store.getStoreId();
        this.username = store.getUser().getUsername();
        this.storeName = store.getStoreName();
        this.phoneNumber = store.getPhoneNumber();
        this.minOrderPrice = store.getMinOrderPrice();
        this.storeStatus = store.getStoreStatus();
        this.address = store.getAddress();
        this.reviewCount = store.getReviewCount();
        this.storeSchedules = storeSchedules;
        this.menus = menus;
    }
}
