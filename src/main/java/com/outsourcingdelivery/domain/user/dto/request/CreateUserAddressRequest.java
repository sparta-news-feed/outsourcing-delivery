package com.outsourcingdelivery.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CreateUserAddressRequest {

    @NotBlank(message = "주소 입력은 필수입니다.")
    private String address;

    @Builder
    private CreateUserAddressRequest(String address) {
        this.address = address;
    }

}
