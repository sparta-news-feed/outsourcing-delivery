package com.outsourcingdelivery.domain.store.dto.request;

import com.outsourcingdelivery.domain.store.enums.StoreStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStoreStatusRequest {
    @NotNull
    private StoreStatus storeStatus;
}
