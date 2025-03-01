package com.outsourcingdelivery.domain.auth.service;

import com.outsourcingdelivery.common.config.PasswordEncoder;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.common.jwt.JwtUtil;
import com.outsourcingdelivery.domain.auth.dto.response.RefreshResponse;
import com.outsourcingdelivery.domain.auth.entity.RefreshToken;
import com.outsourcingdelivery.domain.auth.repository.RefreshTokenRepository;
import com.outsourcingdelivery.domain.user.dto.request.UserCreateRequest;
import com.outsourcingdelivery.domain.user.dto.request.UserLoginRequest;
import com.outsourcingdelivery.domain.auth.dto.response.TokenResponse;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.entity.UserAddress;
import com.outsourcingdelivery.domain.user.enums.UserType;
import com.outsourcingdelivery.domain.user.repository.UserAddressRepository;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Ref;
import java.time.LocalDateTime;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(UserCreateRequest request) {
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .username(request.getUsername())
            .userType(UserType.of(request.getUserType()))
            .phoneNumber(request.getPhoneNumber())
            .build();

        userRepository.save(user);

        UserAddress userAddress = UserAddress.builder()
            .address(request.getAddress())
            .user(user)
            .build();
        userAddressRepository.save(userAddress);

        user.updatePrimaryAddress(userAddress);
    }

    @Transactional
    public TokenResponse login(UserLoginRequest request) {
        User findUser = userRepository.findUserByEmailOrElseThrow(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), findUser.getPassword())) {
            throw new ApplicationException(ErrorCode.INCORRECT_PASSWORD);
        }

        // Access & Refresh Token 생성
        String accessToken = jwtUtil.createAccessToken(findUser.getUserId(), findUser.getEmail(), findUser.getUserType());
        String refreshToken = jwtUtil.createRefreshToken(findUser.getUserId());

        // RefreshToken 저장 및 업데이트
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
        refreshTokenRepository.findByUser(findUser)
            .ifPresentOrElse(
                token -> token.updateToken(refreshToken, expiryDate),
                () -> refreshTokenRepository.save(
                    RefreshToken.builder()
                        .user(findUser)
                        .refreshToken(refreshToken)
                        .expiryDate(expiryDate)
                        .build()
                )
            );

        return new TokenResponse(accessToken, createRefreshTokenCookie(refreshToken));
    }

    @Transactional
    public RefreshResponse refresh(String refreshToken) {
        if (refreshToken == null || jwtUtil.isTokenExpired(refreshToken)) {
            throw new ApplicationException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        RefreshToken savedToken = refreshTokenRepository.findByRefreshTokenOrElseThrow(refreshToken);

        User user = savedToken.getUser();
        String newAccessToken = jwtUtil.createAccessToken(user.getUserId(), user.getEmail(), user.getUserType());

        return new RefreshResponse(newAccessToken);
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)                             // JavaScript 에서 접근 불가 (XSS 공격 방지)
            .secure(true)                               // HTTPS 환경에서만 사용 가능
            .sameSite("Strict")                         // CSRF 공격 방지
            .maxAge(7 * 24 * 60 * 60)     // 7일 동안 유지
            .path("/")                                  // 모든 경로에서 쿠키 접근 가능
            .build();
    }
}