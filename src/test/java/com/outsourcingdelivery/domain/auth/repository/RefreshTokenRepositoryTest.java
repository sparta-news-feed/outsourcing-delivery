package com.outsourcingdelivery.domain.auth.repository;

import com.outsourcingdelivery.common.auth.JwtUtil;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.SpringBootTestSupport;
import com.outsourcingdelivery.domain.auth.entity.RefreshToken;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.enums.UserType;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class RefreshTokenRepositoryTest extends SpringBootTestSupport {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private User savedUser;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        User user = User.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .username("홍길동")
            .userType(UserType.OWNER)
            .phoneNumber("01012345678")
            .build();

        savedUser = userRepository.save(user);
        refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        RefreshToken token = RefreshToken.builder()
            .user(user)
            .refreshToken(refreshToken)
            .expiryDate(LocalDateTime.now().plusDays(7))
            .build();

        refreshTokenRepository.save(token);
    }

    @DisplayName("refresh 토큰으로 유저까지 함께 fetch join 한다.")
    @Test
    void findByRefreshToken1() {
        // when
        RefreshToken findToken = refreshTokenRepository.findByRefreshTokenOrElseThrow(refreshToken);

        // then
        assertThat(findToken.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(findToken.getUser()).isEqualTo(savedUser);
    }

    @DisplayName("refresh 토큰과 DB에 있는 refresh 토큰이 일치하지 않으면 예외가 발생한다.")
    @Test
    void findByRefreshToken2() {
        // when & then
        assertThatThrownBy(() -> refreshTokenRepository.findByRefreshTokenOrElseThrow("testToken"))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_TOKEN.getMessage());
    }
}