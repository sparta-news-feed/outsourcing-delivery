package com.outsourcingdelivery.domain.auth.repository;

import com.outsourcingdelivery.common.auth.JwtUtil;
import com.outsourcingdelivery.common.config.TestConfig;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.auth.entity.RefreshToken;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.enums.UserType;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * RefreshToken Repository 테스트만 @DataJpaTest 적용시켜봤습니다.
 * 따로 테스트용 DB를 만드는것도 다음 플젝부턴 생각해봐야겠습니다.
 */

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @DisplayName("refresh 토큰으로 사용자 정보를 조회할 수 있다.")
    @Test
    void findByRefreshToken1() {
        // given
        User user = User.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .username("홍길동")
            .userType(UserType.OWNER)
            .phoneNumber("01012345678")
            .build();

        User savedUser = userRepository.save(user);

        String mockRefresh = "mock-refresh-token";
        when(jwtUtil.createRefreshToken(savedUser.getUserId())).thenReturn(mockRefresh);
        RefreshToken token = RefreshToken.builder()
            .user(user)
            .refreshToken(mockRefresh)
            .expiryDate(LocalDateTime.now().plusDays(7))
            .build();

        refreshTokenRepository.save(token);

        // when
        RefreshToken findToken = refreshTokenRepository.findByRefreshTokenOrElseThrow(mockRefresh);

        // then
        assertThat(findToken.getRefreshToken()).isEqualTo(mockRefresh);
        assertThat(findToken.getUser()).isEqualTo(savedUser);
    }

    @DisplayName("존재하지 않는 refresh 토큰으로 조회하면 예외가 발생한다.")
    @Test
    void findByRefreshToken2() {
        // when & then
        assertThatThrownBy(() -> refreshTokenRepository.findByRefreshTokenOrElseThrow("testToken"))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_TOKEN.getMessage());
    }
}