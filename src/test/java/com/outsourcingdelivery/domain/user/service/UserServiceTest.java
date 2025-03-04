package com.outsourcingdelivery.domain.user.service;

import com.outsourcingdelivery.common.config.PasswordEncoder;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.SpringBootTestSupport;
import com.outsourcingdelivery.domain.auth.dto.request.SignInRequest;
import com.outsourcingdelivery.domain.auth.dto.request.SignUpRequest;
import com.outsourcingdelivery.domain.auth.service.AuthService;
import com.outsourcingdelivery.domain.user.dto.request.UpdatePasswordRequest;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.entity.UserAddress;
import com.outsourcingdelivery.domain.user.enums.UserType;
import com.outsourcingdelivery.domain.user.repository.UserAddressRepository;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Transactional
class UserServiceTest extends SpringBootTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("비밀번호 변경 요청시 성공적으로 변경된다.")
    @Test
    void updatePassword1() throws Exception {
        // given
        User user = User.builder()
            .email("abc@abc.com")
            .password(passwordEncoder.encode("Password1234!"))
            .userType(UserType.OWNER)
            .build();

        User save = userRepository.save(user);

        AuthUser authUser = AuthUser.builder()
            .userId(save.getUserId())
            .build();

        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password1234!")
            .newPassword("Password12345!")
            .build();

        // when
        userService.updatePassword(authUser, request);
        User findUser = userRepository.findByIdOrElseThrow(save.getUserId(), ErrorCode.NOT_FOUND_USER);

        // then
        assertThat(passwordEncoder.matches("Password12345!", findUser.getPassword())).isTrue();
    }

    @DisplayName("비밀번호 변경시 기존 비밀번호와 일치하지 않으면 예외가 발생한다.")
    @Test
    void updatePassword2() throws Exception {
        // given
        User user = User.builder()
            .email("abc@abc.com")
            .password(passwordEncoder.encode("Password1234!"))
            .userType(UserType.OWNER)
            .build();

        User save = userRepository.save(user);

        AuthUser authUser = AuthUser.builder()
            .userId(save.getUserId())
            .build();

        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password12345!")
            .newPassword("Password123456!")
            .build();

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(authUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.INCORRECT_PASSWORD.getMessage());
    }

    @DisplayName("비밀번호 변경시 기존 비밀번호와 새비밀번호가 같으면 예외가 발생한다.")
    @Test
    void updatePassword3() throws Exception {
        // given
        User user = User.builder()
            .email("abc@abc.com")
            .password(passwordEncoder.encode("Password1234!"))
            .userType(UserType.OWNER)
            .build();

        User save = userRepository.save(user);

        AuthUser authUser = AuthUser.builder()
            .userId(save.getUserId())
            .build();

        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password12345!")
            .newPassword("Password12345!")
            .build();

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(authUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.SAME_AS_OLD_PASSWORD.getMessage());
    }

    @DisplayName("유저의 기본 주소지 변경에 성공한다.")
    @Test
    void updatePrimaryAddress1() throws Exception {
        // given
        User user = User.builder()
            .email("abc@abc.com")
            .password(passwordEncoder.encode("Password1234!"))
            .userType(UserType.OWNER)
            .build();

        UserAddress oldAddress = UserAddress.builder()
            .user(user)
            .address("서울")
            .build();

        user.updatePrimaryAddress(oldAddress);
        User save = userRepository.save(user);

        AuthUser authUser = AuthUser.builder()
            .userId(save.getUserId())
            .build();

        UserAddress newAddress = UserAddress.builder()
            .user(user)
            .address("부산")
            .build();

        userAddressRepository.saveAll(List.of(oldAddress, newAddress));

        // when
        userService.updatePrimaryAddress(authUser, newAddress.getUserAddressId());

        // then
        assertThat(save.getPrimaryAddress())
            .extracting("userAddressId", "address")
            .containsExactly(newAddress.getUserAddressId(), newAddress.getAddress());
    }

    @DisplayName("유저의 기본 주소지를 변경할때 동일한 user_address_id로 요청하면 예외가 발생한다.")
    @Test
    void updatePrimaryAddress2() throws Exception {
        // given
        User user = User.builder()
            .email("abc@abc.com")
            .password(passwordEncoder.encode("Password1234!"))
            .userType(UserType.OWNER)
            .build();

        UserAddress oldAddress = UserAddress.builder()
            .user(user)
            .address("서울")
            .build();

        user.updatePrimaryAddress(oldAddress);
        User save = userRepository.save(user);

        AuthUser authUser = AuthUser.builder()
            .userId(save.getUserId())
            .build();

        UserAddress newAddress = UserAddress.builder()
            .user(user)
            .address("부산")
            .build();

        userAddressRepository.saveAll(List.of(oldAddress, newAddress));

        // when & then
        assertThatThrownBy(() -> userService.updatePrimaryAddress(authUser, oldAddress.getUserAddressId()))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.PRIMARY_ADDRESS_ALREADY_SET.getMessage());

    }

    private SignUpRequest createSignUpRequest(String email, String password, String userType) {
        return SignUpRequest.builder()
            .email(email)
            .password(password)
            .username("홍길동")
            .userType(userType)
            .phoneNumber("01012345678")
            .address("서울")
            .build();
    }

    private SignInRequest createSignInRequest(String email, String password, String userType) {
        return SignInRequest.builder()
            .email(email)
            .password(password)
            .userType(userType)
            .build();
    }
}