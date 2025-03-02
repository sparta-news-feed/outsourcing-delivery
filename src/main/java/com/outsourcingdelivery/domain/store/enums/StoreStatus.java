package com.outsourcingdelivery.domain.store.enums;

import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;

import java.util.Arrays;

public enum StoreStatus {
    READY, CLOSED, OPEN;

    public static StoreStatus of(String type) {
        return Arrays.stream(StoreStatus.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_STORE_STATUS_ENUM_VALUE));
    }
}
