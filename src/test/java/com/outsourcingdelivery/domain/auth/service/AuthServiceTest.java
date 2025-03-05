package com.outsourcingdelivery.domain.auth.service;

import com.outsourcingdelivery.common.auth.JwtUtil;
import com.outsourcingdelivery.common.config.PasswordEncoder;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.SpringBootTestSupport;
import com.outsourcingdelivery.domain.auth.dto.request.WithDrawRequest;
import com.outsourcingdelivery.domain.auth.dto.response.AccessTokenResponse;
import com.outsourcingdelivery.domain.auth.dto.response.TokenResponse;
import com.outsourcingdelivery.domain.auth.entity.RefreshToken;
import com.outsourcingdelivery.domain.auth.repository.RefreshTokenRepository;
import com.outsourcingdelivery.domain.auth.dto.request.SignUpRequest;
import com.outsourcingdelivery.domain.auth.dto.request.SignInRequest;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.entity.UserAddress;
import com.outsourcingdelivery.domain.user.enums.UserType;
import com.outsourcingdelivery.domain.user.repository.UserAddressRepository;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class AuthServiceTest extends SpringBootTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email("abc@abc.com")
            .password(passwordEncoder.encode("Password1234!"))
            .userType(UserType.OWNER)
            .phoneNumber("01012345678")
            .username("홍길동")
            .build();
    }

    /**
     * 아래 코드는 @Transactional 을 사용하지 않을시 꼭 해당 TestCode 내에서 사용한 Repository 내부에 데이터들을 지워줘야 다른 TestCode 에 영향이 가지 않습니다.
     * 주의사항으로는 데이터를 비워주는 순서를 생각해야합니다. 연관관계 때문에 삭제 순서를 신경쓰지 않을 시 오류가 발생합니다.
     * 아직 개념을 잘 모르겠다면 class 상단에 @Transactional 을 꼭 붙여서 사용하시면 됩니다.
     * 다만 실제 프로덕션 코드인 main 폴더 내에서의 Service 에 @Transactional 코드가 영향이 가기 때문에 main 폴더 내에 깜빡하지마시고 CRUD 부분에서의 CUD 쪽에 @Transactional 을 붙였는지 확인바랍니다.
     */
//    @AfterEach
//    void tearDown() {
//        refreshTokenRepository.deleteAllInBatch();
//        userAddressRepository.deleteAllInBatch();
//        userRepository.deleteAllInBatch();
//    }
    @DisplayName("회원가입시 유저가 정상적으로 생성이 된다.")
    @Test
    void signup1() throws Exception {
        // given
        SignUpRequest request = createSignUpRequest("abc@abc.com", "Password1234!", UserType.OWNER);
        Long userId = authService.signup(request);

        // when
        User findUser = userRepository.findByIdOrElseThrow(userId, ErrorCode.NOT_FOUND_USER);
        UserAddress findUserAddress = userAddressRepository.findByIdOrElseThrow(findUser.getPrimaryAddress().getUserAddressId(), ErrorCode.USER_ADDRESS_NOT_FOUND);

        /**
         * 예시를 보여드리기 위해 contains 관련 메서드를 모두 사용했지만 필요한 것 하나만 사용하셔도 됩니다.
         * 해당 방식들로 해도 다 통과한다는 것을 보여드리기위해 모두 사용했습니다.
         * 우리팀 화이팅!!
         */

        // then
        assertThat(findUser)
            .extracting("userId", "email", "username", "userType", "primaryAddress")        // 검사할 필드 변수 이름들 지정
            .containsExactlyInAnyOrder("abc@abc.com", findUser.getUserId(), "홍길동", UserType.OWNER, findUserAddress)
            // - extracting() 에 지정된 모든 필드를 검사
            // - containsExactlyInAnyOrder, 순서는 상관없지만, 지정한 모든 값이 포함되어 있어야함 (값이 하나라도 다르면 실패)
            .contains(findUser.getUserId())
            // contains 는 리스트에 해당 값이 포함되어 있기만 하면됨, 순서, 갯수 상관없음
            .containsExactly(findUser.getUserId(), "abc@abc.com", "홍길동", UserType.OWNER, findUserAddress);
        // - extracting() 에 지정된 필드 순서와 동일하게 값을 넣어야함
        // - 값이 하나라도 다르거나 순서가 다르면 실패

        assertThat(findUserAddress)
            .extracting("userAddressId", "address", "user")
            .containsExactly(findUserAddress.getUserAddressId(), "서울", findUser);

    }

    @DisplayName("회원가입시 OWNER, USER 타입이 다를때 이메일이 중복이어도 정상적으로 가입된다.")
    @Test
    void signup2() throws Exception {
        // given
        String userEmail = "abc@abc.com";

        SignUpRequest owner = createSignUpRequest(userEmail, "Password1234!", UserType.OWNER);
        SignUpRequest user = createSignUpRequest(userEmail, "Password1234!", UserType.USER);

        authService.signup(owner);
        authService.signup(user);

        // when
        User findOwner = userRepository.findUserByEmailAndUserTypeOrElseThrow(userEmail, UserType.OWNER);
        User findUser = userRepository.findUserByEmailAndUserTypeOrElseThrow(userEmail, UserType.USER);

        // then
        assertThat(findOwner)
            .extracting("email", "userType")
            .containsExactly(userEmail, UserType.OWNER);

        assertThat(findUser)
            .extracting("email", "userType")
            .containsExactly(userEmail, UserType.USER);
    }

    @DisplayName("회원가입시 유저가 같은 유저 타입에 중복된 이메일로 가입할시 예외가 발생한다.")
    @Test
    void signup3() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .userType(UserType.OWNER)
            .phoneNumber("01012345678")
            .address("서울")
            .username("홍길동")
            .build();

        authService.signup(request);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.DUPLICATE_EMAIL.getMessage());
    }

    @DisplayName("회원가입시 이미 탈퇴한 유저가 재회원가입하면 예외가 발생한다.")
    @Test
    void signup4() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .userType(UserType.OWNER)
            .phoneNumber("01012345678")
            .address("서울")
            .username("홍길동")
            .build();
        Long userId = authService.signup(request);

        SignInRequest loginRequest = createSignInRequest("abc@abc.com", "Password1234!", UserType.OWNER);
        authService.login(loginRequest);

        WithDrawRequest withDrawRequest = WithDrawRequest.builder()
            .password("Password1234!")
            .build();

        AuthUser authUser = AuthUser.builder()
            .userId(userId)
            .build();

        authService.withdraw(authUser, withDrawRequest);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.DELETED_USER_CANNOT_REGISTER.getMessage());
    }

    @DisplayName("로그인시 accessToken, refreshToken 이 정상적으로 생성된다.")
    @Test
    void login1() throws Exception {
        // given
        SignUpRequest createRequest = createSignUpRequest("abc@abc.com", "Password1234!", UserType.OWNER);
        SignInRequest loginRequest = createSignInRequest("abc@abc.com", "Password1234!", UserType.OWNER);

        authService.signup(createRequest);

        // when
        TokenResponse response = authService.login(loginRequest);

        // then
        assertThat(response.getAccessToken())
            .isNotNull()
            .contains("Bearer");

        assertThat(response.getRefreshToken())
            .isNotNull();
    }

    @DisplayName("로그인시 기존 refreshToken이 있으면 업데이트된다.")
    @Test
    void login2() throws Exception {
        // given
        SignUpRequest createRequest = createSignUpRequest("abc@abc.com", "Password1234!", UserType.OWNER);
        SignInRequest loginRequest = createSignInRequest("abc@abc.com", "Password1234!", UserType.OWNER);

        authService.signup(createRequest);

        // when
        TokenResponse first = authService.login(loginRequest);
        String oldRefreshToken = first.getRefreshToken().getValue();

        TokenResponse second = authService.login(loginRequest);
        String newRefreshToken = second.getRefreshToken().getValue();

        // then
        assertThat(newRefreshToken)
            .isNotEqualTo(oldRefreshToken)
            .isNotNull();
    }

    @DisplayName("로그인시 DB에 저장된 비밀번호와 다를시 예외 발생")
    @Test
    void login3() throws Exception {
        // given
        userRepository.save(user);
        SignInRequest loginRequest = createSignInRequest("abc@abc.com", "Password123!", UserType.OWNER);

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.INCORRECT_PASSWORD.getMessage());

    }

    @DisplayName("로그인시 DB에 저장된 이메일 혹은 유저타입이 다를시 예외 발생")
    @Test
    void login4() throws Exception {
        // given
        SignUpRequest createRequest1 = createSignUpRequest("abc@abc.com", "Password1234!", UserType.OWNER);
        SignInRequest loginRequest1 = createSignInRequest("abc@abc.com", "Password1234!", UserType.USER);

        SignUpRequest createRequest2 = createSignUpRequest("abc1@abc.com", "Password1234!", UserType.OWNER);
        SignInRequest loginRequest2 = createSignInRequest("abc2@abc.com", "Password1234!", UserType.OWNER);

        authService.signup(createRequest1);
        authService.signup(createRequest2);

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest1))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_USER.getMessage());

        assertThatThrownBy(() -> authService.login(loginRequest2))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_USER.getMessage());
    }

    @DisplayName("로그인시 이미 탈퇴한 유저가 로그인을 시도하면 예외가 발생한다.")
    @Test
    void login5() throws Exception {
        // given
        User savedUser = userRepository.save(user);
        savedUser.deleteUser();

        SignInRequest signInRequest = createSignInRequest("abc@abc.com", "Password1234!", UserType.OWNER);

        // when & then
        assertThatThrownBy(() -> authService.login(signInRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.ALREADY_DELETED_USER.getMessage());
    }

    @DisplayName("RefreshToken 이 있을때 정상적으로 새로운 AccessToken 재발급 성공")
    @Test
    void refresh1() throws Exception {
        // given
        User savedUser = userRepository.save(user);
        String refreshToken = jwtUtil.createRefreshToken(savedUser.getUserId());
        RefreshToken token = RefreshToken.builder()
            .refreshToken(refreshToken)
            .expiryDate(LocalDateTime.now().plusDays(7))
            .user(savedUser)
            .build();

        RefreshToken savedToken = refreshTokenRepository.save(token);

        // when
        AccessTokenResponse response = authService.refresh(savedToken.getRefreshToken());

        // then
        assertThat(response.getAccessToken()).isNotNull()
            .contains("Bearer");
    }

    @DisplayName("RefreshToken 이 없거나 비어있거나 만료시 예외가 발생한다.")
    @Test
    void refresh2() throws Exception {
        // given
        Instant now = Instant.now();
        Instant expiration = now.minus(1, ChronoUnit.DAYS);

        String refreshToken = Jwts.builder()
            .subject(String.valueOf(1L))
            .claims(Map.of())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(jwtUtil.getKey())
            .compact();

        // when & then
        assertThatThrownBy(() -> authService.refresh(refreshToken))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.EXPIRED_REFRESH_TOKEN.getMessage());

        assertThatThrownBy(() -> authService.refresh(""))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.EXPIRED_REFRESH_TOKEN.getMessage());

        assertThatThrownBy(() -> authService.refresh(null))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.EXPIRED_REFRESH_TOKEN.getMessage());
    }

    @DisplayName("로그아웃시 리프레시 토큰이 정상적으로 만료된다.")
    @Test
    void logout1() throws Exception {
        // given
        User savedUser = userRepository.save(user);

        AuthUser authUser = AuthUser.builder()
            .userId(savedUser.getUserId())
            .build();

        // when
        ResponseCookie response = authService.logout(authUser);

        // then
        assertThat(response.getValue()).isEqualTo("");
        assertThat(response.getMaxAge()).isEqualTo(Duration.ZERO);
    }

    @DisplayName("RefreshToken이 없는 유저가 로그아웃해도 정상적으로 응답한다.")
    @Test
    void logout2() throws Exception {
        // given
        User savedUser = userRepository.save(user);

        AuthUser authUser = AuthUser.builder()
            .userId(savedUser.getUserId())
            .build();

        // when
        ResponseCookie response = authService.logout(authUser);

        // then
        assertThat(response.getValue()).isEqualTo("");
        assertThat(refreshTokenRepository.findByUser(user)).isEmpty();
    }

    @DisplayName("존재하지 않는 유저가 로그아웃할 경우 예외가 발생한다.")
    @Test
    void logout3() throws Exception {
        // given
        AuthUser authUser = AuthUser.builder()
            .userId(9999L)
            .build();

        // when & then
        assertThatThrownBy(() -> authService.logout(authUser))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_USER.getMessage() + " id = 9999");
    }

    @DisplayName("회원탈퇴시 유저의 deletedAt이 null이 아니고, 기본 주소는 null 이되면서, 회원주소테이블, 그리고 RefreshToken은 함께 삭제된다.")
    @Test
    void withdraw1() throws Exception {
        // given
        User savedUser = userRepository.save(user);

        AuthUser authUser = AuthUser.builder()
            .userId(savedUser.getUserId())
            .build();

        WithDrawRequest request = WithDrawRequest.builder()
            .password("Password1234!")
            .build();

        // when
        ResponseCookie response = authService.withdraw(authUser, request);
        List<UserAddress> addressList = userAddressRepository.findAllByUserId(savedUser.getUserId());

        boolean isRefreshTokenDeleted = refreshTokenRepository.findByUser(user).isEmpty();

        // then
        assertThat(user.getPrimaryAddress()).isNull();
        assertThat(user.getDeletedAt()).isNotNull();
        assertThat(addressList).hasSize(0).isEmpty();
        assertThat(isRefreshTokenDeleted).isTrue();
        assertThat(response.getValue()).isEqualTo("");
        assertThat(response.getMaxAge()).isEqualTo(Duration.ZERO);
    }

    @DisplayName("회원탈퇴시 이미 탈퇴한 유저라면 예외가 발생한다.")
    @Test
    void withdraw2() throws Exception {
        // given
        user.deleteUser();
        User savedUser = userRepository.save(user);

        AuthUser authUser = AuthUser.builder()
            .userId(savedUser.getUserId())
            .build();

        WithDrawRequest request = WithDrawRequest.builder()
            .password("Password1234!")
            .build();

        // when & then
        assertThatThrownBy(() -> authService.withdraw(authUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.ALREADY_DELETED_USER.getMessage());
    }

    @DisplayName("회원탈퇴시 유저의 비밀번호가 틀리면 예외가 발생한다.")
    @Test
    void withdraw3() throws Exception {
        // given
        User savedUser = userRepository.save(user);

        AuthUser authUser = AuthUser.builder()
            .userId(savedUser.getUserId())
            .build();

        WithDrawRequest request = WithDrawRequest.builder()
            .password("Password123!")
            .build();

        // when & then
        assertThatThrownBy(() -> authService.withdraw(authUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.INCORRECT_PASSWORD.getMessage());
    }

    private SignUpRequest createSignUpRequest(String email, String password, UserType userType) {
        return SignUpRequest.builder()
            .email(email)
            .password(password)
            .username("홍길동")
            .userType(userType)
            .phoneNumber("01012345678")
            .address("서울")
            .build();
    }

    private SignInRequest createSignInRequest(String email, String password, UserType userType) {
        return SignInRequest.builder()
            .email(email)
            .password(password)
            .userType(userType)
            .build();
    }
}