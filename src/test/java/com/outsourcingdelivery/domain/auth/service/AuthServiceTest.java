package com.outsourcingdelivery.domain.auth.service;

import com.outsourcingdelivery.common.config.PasswordEncoder;
import com.outsourcingdelivery.domain.IntegrationTestSupport;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.enums.UserType;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("회원가입시 유저가 정상적으로 생성이 된다.")
    @Test
    void signup1() throws Exception {
        // given
        User user = User.builder()
            .email("abc@abc.com")
            .password(passwordEncoder.encode("1234"))
            .username("홍길동")
            .userType(UserType.OWNER)
            .build();

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser)
            .extracting("userId", "email", "username", "userType")        // 검사할 필드 변수 이름들 지정
            .containsExactlyInAnyOrder("abc@abc.com", 1L, "홍길동", UserType.OWNER)
            // - extracting() 에 지정된 모든 필드를 검사
            // - containsExactlyInAnyOrder, 순서는 상관없지만, 지정한 모든 값이 포함되어 있어야함 (값이 하나라도 다르면 실패)
            .contains(1L)
            // contains 는 리스트에 해당 값이 포함되어 있기만 하면됨, 순서, 갯수 상관없음
            .containsExactly(1L, "abc@abc.com", "홍길동", UserType.OWNER);
        // - extracting() 에 지정된 필드 순서와 동일하게 값을 넣어야함
        // - 값이 하나라도 다르거나 순서가 다르면 실패
    }
}