package com.outsourcingdelivery.domain.user.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.user.dto.request.CreateUserAddressRequest;
import com.outsourcingdelivery.domain.user.dto.request.UpdateUserAddressRequest;
import com.outsourcingdelivery.domain.user.dto.response.UserAddressResponse;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.entity.UserAddress;
import com.outsourcingdelivery.domain.user.repository.UserAddressRepository;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createUserAddress(AuthUser authUser, @Valid CreateUserAddressRequest request) {
        List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(authUser.getUserId());
        if (userAddressList.size() >= 10) {
            throw new ApplicationException(ErrorCode.MAX_USER_ADDRESS_LIMIT_EXCEEDED);
        }

        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.USER_NOT_FOUND);

        UserAddress userAddress = UserAddress.builder()
            .address(request.getAddress())
            .user(user)
            .build();

        UserAddress saved = userAddressRepository.save(userAddress);
        return saved.getUserAddressId();
    }

    public List<UserAddressResponse> getAllUserAddress(AuthUser authUser) {
        return userAddressRepository.findAllByUserId(authUser.getUserId())
            .stream()
            .map(UserAddressResponse::toDto)
            .toList();
    }

    @Transactional
    public void updateUserAddress(AuthUser authUser, Long addressId, @Valid UpdateUserAddressRequest request) {
        UserAddress userAddress = userAddressRepository.findByIdWithUser(addressId)
            .orElseThrow(() -> new ApplicationException(ErrorCode.USER_ADDRESS_NOT_FOUND));

        if (!userAddress.getUser().getUserId().equals(authUser.getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ADDRESS_UPDATE);
        }

        userAddress.updateAddress(request.getAddress());
    }

    @Transactional
    public void deleteUserAddress(AuthUser authUser, Long addressId) {
        UserAddress userAddress = userAddressRepository.findByIdWithUser(addressId)
            .orElseThrow(() -> new ApplicationException(ErrorCode.USER_ADDRESS_NOT_FOUND));

        if (!userAddress.getUser().getUserId().equals(authUser.getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ADDRESS_UPDATE);
        }

        User user = userAddress.getUser();
        if (user.getPrimaryAddress().getUserAddressId().equals(addressId)) {
            throw new ApplicationException(ErrorCode.CANNOT_DELETE_PRIMARY_ADDRESS);
        }

        userAddressRepository.delete(userAddress);
    }
}
