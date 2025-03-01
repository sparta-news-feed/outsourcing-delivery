package com.outsourcingdelivery.domain.user.service;

import com.outsourcingdelivery.domain.user.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;


}
