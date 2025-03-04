package com.outsourcingdelivery.domain.user.dto.response;

import com.outsourcingdelivery.domain.user.entity.UserAddress;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserAddressResponse {

    private final Long userAddressId;

    private final String address;

    @Builder
    private UserAddressResponse(Long userAddressId, String address) {
        this.userAddressId = userAddressId;
        this.address = address;
    }

    public static UserAddressResponse toDto(UserAddress userAddress) {
        return UserAddressResponse.builder()
            .userAddressId(userAddress.getUserAddressId())
            .address(userAddress.getAddress())
            .build();
    }
}
