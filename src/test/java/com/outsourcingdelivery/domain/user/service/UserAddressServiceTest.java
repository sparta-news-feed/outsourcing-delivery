package com.outsourcingdelivery.domain.user.service;

import com.outsourcingdelivery.common.config.PasswordEncoder;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.SpringBootTestSupport;
import com.outsourcingdelivery.domain.user.dto.request.CreateUserAddressRequest;
import com.outsourcingdelivery.domain.user.dto.request.UpdateUserAddressRequest;
import com.outsourcingdelivery.domain.user.dto.response.UserAddressResponse;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.entity.UserAddress;
import com.outsourcingdelivery.domain.user.enums.UserType;
import com.outsourcingdelivery.domain.user.repository.UserAddressRepository;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@Transactional
class UserAddressServiceTest extends SpringBootTestSupport {

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User savedUser;
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        User user = User.builder()
            .email("abc@abc.com")
            .password(passwordEncoder.encode("Password1234!"))
            .userType(UserType.OWNER)
            .phoneNumber("01012345678")
            .username("홍길동")
            .build();

        savedUser = userRepository.save(user);

        authUser = AuthUser.builder()
            .userId(savedUser.getUserId())
            .build();
    }

    @DisplayName("유저의 새로운 주소 생성을 성공한다.")
    @Test
    void createUserAddress1() throws Exception {
        // given
        CreateUserAddressRequest request = createUserAddressRequest("서울");

        // when
        Long userAddressId = userAddressService.createUserAddress(authUser, request);
        UserAddress findUserAddress = userAddressRepository.findByIdOrElseThrow(userAddressId, ErrorCode.USER_ADDRESS_NOT_FOUND);

        // then
        assertThat(findUserAddress)
            .extracting("userAddressId", "address", "user")
            .containsExactly(userAddressId, "서울", savedUser);
    }

    @DisplayName("존재하지 않는 유저로 주소 생성시 예외가 발생한다.")
    @Test
    void createUserAddress2() throws Exception {
        // given
        AuthUser authUser = AuthUser.builder()
            .userId(-1L)
            .build();

        CreateUserAddressRequest request = createUserAddressRequest("서울");

        // when & then
        assertThatThrownBy(() -> userAddressService.createUserAddress(authUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_USER.getMessage() + " id = -1");

    }

    @DisplayName("주소 최대 생성 갯수 10개를 넘어가면 예외가 발생한다.")
    @Test
    void createUserAddress3() throws Exception {
        // given
        List<UserAddress> userAddresses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            userAddresses.add(createUserAddress("서울" + i));
        }
        userAddressRepository.saveAll(userAddresses);

        CreateUserAddressRequest request = createUserAddressRequest("서울");

        // when & then
        assertThatThrownBy(() -> userAddressService.createUserAddress(authUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.MAX_USER_ADDRESS_LIMIT_EXCEEDED.getMessage());

    }

    @DisplayName("유저의 주소 목록을 성공적으로 가져온다.")
    @Test
    void getAllUserAddress1() throws Exception {
        // given
        UserAddress userAddress1 = createUserAddress("서울");
        UserAddress userAddress2 = createUserAddress("강남");
        UserAddress userAddress3 = createUserAddress("경기");

        userAddressRepository.saveAll(List.of(userAddress1, userAddress2, userAddress3));

        // when
        List<UserAddressResponse> response = userAddressService.getAllUserAddress(authUser);

        // then
        assertThat(response).hasSize(3)
            .extracting("userAddressId", "address")
            .containsExactly(
                tuple(userAddress1.getUserAddressId(), "서울"),
                tuple(userAddress2.getUserAddressId(), "강남"),
                tuple(userAddress3.getUserAddressId(), "경기")
            );
    }

    @DisplayName("유저의 주소가 없을 때 빈 리스트를 반환한다.")
    @Test
    void getAllUserAddress2() throws Exception {
        // when
        List<UserAddressResponse> response = userAddressService.getAllUserAddress(authUser);

        // then
        assertThat(response).isEmpty();
    }

    @DisplayName("특정 유저의 주소 업데이트가 성공한다.")
    @Test
    void updateUserAddress1() throws Exception {
        // given
        UserAddress userAddress = UserAddress.builder()
            .address("서울")
            .user(savedUser)
            .build();

        UserAddress savedAddress = userAddressRepository.save(userAddress);

        UpdateUserAddressRequest request = UpdateUserAddressRequest.builder()
            .address("경기도")
            .build();
        // when
        userAddressService.updateUserAddress(authUser, savedAddress.getUserAddressId(), request);
        UserAddress findAddress = userAddressRepository.findByIdOrElseThrow(savedAddress.getUserAddressId(), ErrorCode.USER_ADDRESS_NOT_FOUND);

        // then
        assertThat(findAddress.getAddress()).isEqualTo("경기도");
        assertThat(savedAddress.getAddress()).isNotEqualTo("서울");
    }

    @DisplayName("자신의 주소를 변경하는 것이 아닌 경우 예외가 발생한다.")
    @Test
    void updateUserAddress2() throws Exception {
        // given
        AuthUser authUser = AuthUser.builder()
            .userId(savedUser.getUserId() + 1)
            .build();

        UserAddress userAddress = createUserAddress("서울");

        UserAddress savedAddress = userAddressRepository.save(userAddress);

        UpdateUserAddressRequest request = UpdateUserAddressRequest.builder()
            .address("경기도")
            .build();

        // when & then
        assertThatThrownBy(() -> userAddressService.updateUserAddress(authUser, savedAddress.getUserAddressId(), request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.UNAUTHORIZED_ADDRESS_UPDATE.getMessage());
    }

    @DisplayName("선택한 주소가 정상적으로 삭제된다.")
    @Test
    void deleteUserAddress1() throws Exception {
        // given
        UserAddress userAddress1 = createUserAddress("서울");
        UserAddress userAddress2 = createUserAddress("강남");

        savedUser.updatePrimaryAddress(userAddress1);

        userAddressRepository.saveAll(List.of(userAddress1, userAddress2));

        // when
        userAddressService.deleteUserAddress(authUser, userAddress2.getUserAddressId());

        // then
        List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(authUser.getUserId());
        assertThat(userAddressList).hasSize(1)
            .extracting("userAddressId", "address")
            .containsExactly(tuple(userAddress1.getUserAddressId(), "서울"));
    }

    @DisplayName("유저의 기본 주소를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void deleteUserAddress2() throws Exception {
        // given
        UserAddress userAddress1 = createUserAddress("서울");
        UserAddress userAddress2 = createUserAddress("강남");

        savedUser.updatePrimaryAddress(userAddress1);

        userAddressRepository.saveAll(List.of(userAddress1, userAddress2));

        // when & then
        assertThatThrownBy(() -> userAddressService.deleteUserAddress(authUser, userAddress1.getUserAddressId()))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.CANNOT_DELETE_PRIMARY_ADDRESS.getMessage());

    }

    @DisplayName("자신의 주소를 삭제하는 것이 아닌 경우 예외가 발생한다.")
    @Test
    void deleteUserAddress3() throws Exception {
        // given
        AuthUser authUser = AuthUser.builder()
            .userId(savedUser.getUserId() + 1)
            .build();

        UserAddress userAddress1 = createUserAddress("서울");
        UserAddress userAddress2 = createUserAddress("강남");

        savedUser.updatePrimaryAddress(userAddress1);

        userAddressRepository.saveAll(List.of(userAddress1, userAddress2));

        // when & then
        assertThatThrownBy(() -> userAddressService.deleteUserAddress(authUser, userAddress1.getUserAddressId()))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.UNAUTHORIZED_ADDRESS_UPDATE.getMessage());

    }

    private UserAddress createUserAddress(String address) {
        return UserAddress.builder()
            .user(savedUser)
            .address(address)
            .build();
    }

    private CreateUserAddressRequest createUserAddressRequest(String address) {
        return CreateUserAddressRequest.builder()
            .address(address)
            .build();
    }

}