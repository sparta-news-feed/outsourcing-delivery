package com.outsourcingdelivery.domain.user.enums;

import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;

import java.util.Arrays;

public enum UserType {
    USER, OWNER;

    public static UserType of(String type) {
        return Arrays.stream(UserType.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_USER_TYPE));
    }
}
