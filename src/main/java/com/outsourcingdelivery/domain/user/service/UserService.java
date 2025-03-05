package com.outsourcingdelivery.domain.user.service;

import com.outsourcingdelivery.common.config.PasswordEncoder;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.user.dto.request.UpdatePasswordRequest;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.entity.UserAddress;
import com.outsourcingdelivery.domain.user.repository.UserAddressRepository;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updatePassword(AuthUser authUser, UpdatePasswordRequest request) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);

        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new ApplicationException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ApplicationException(ErrorCode.INCORRECT_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void updatePrimaryAddress(AuthUser authUser, Long addressId) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);
        UserAddress userAddress = userAddressRepository.findByIdOrElseThrow(addressId, ErrorCode.NOT_FOUND_USER_ADDRESS);

        if (user.getPrimaryAddress().getUserAddressId().equals(addressId)) {
            throw new ApplicationException(ErrorCode.PRIMARY_ADDRESS_ALREADY_SET);
        }

        user.updatePrimaryAddress(userAddress);
    }
}
