package com.outsourcingdelivery.domain.user.service;

import com.outsourcingdelivery.common.config.PasswordEncoder;
import com.outsourcingdelivery.domain.embedded.Address;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.enums.UserType;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void signup(String email, String password, String username, String userType, Address address) {
        User user = new User(
                email,
                passwordEncoder.encode(password),
                username,
                UserType.of(userType),
                address
        );
        userRepository.save(user);
    }
}
