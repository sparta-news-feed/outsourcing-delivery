package com.outsourcingdelivery.domain.auth.service;

import com.outsourcingdelivery.common.config.PasswordEncoder;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.common.auth.JwtUtil;
import com.outsourcingdelivery.domain.auth.dto.request.WithDrawRequest;
import com.outsourcingdelivery.domain.auth.dto.response.AccessTokenResponse;
import com.outsourcingdelivery.domain.auth.entity.RefreshToken;
import com.outsourcingdelivery.domain.auth.repository.RefreshTokenRepository;
import com.outsourcingdelivery.domain.auth.dto.request.SignUpRequest;
import com.outsourcingdelivery.domain.auth.dto.request.SignInRequest;
import com.outsourcingdelivery.domain.auth.dto.response.TokenResponse;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.entity.UserAddress;
import com.outsourcingdelivery.domain.user.repository.UserAddressRepository;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private static final Long REFRESH_TOKEN_EXPIRY_DAY = 7 * 24 * 24 * 60L;     // 7일

    @Transactional
    public Long signup(SignUpRequest request) {
        userRepository.findUserByEmailAndUserType(request.getEmail(), request.getUserType())
            .ifPresent(user -> {
                if (user.getDeletedAt() != null) {
                    throw new ApplicationException(ErrorCode.DELETED_USER_CANNOT_REGISTER);
                }

                throw new ApplicationException(ErrorCode.DUPLICATE_EMAIL);
            });

        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .username(request.getUsername())
            .userType(request.getUserType())
            .phoneNumber(request.getPhoneNumber())
            .build();

        userRepository.save(user);

        UserAddress userAddress = UserAddress.builder()
            .address(request.getAddress())
            .build();

        user.updatePrimaryAddress(userAddress);
        userAddressRepository.save(userAddress);

        return user.getUserId();
    }

    @Transactional
    public TokenResponse login(SignInRequest request, LocalDateTime expiryDate) {
        User findUser = userRepository.findUserByEmailAndUserTypeOrElseThrow(
            request.getEmail(),
            request.getUserType()
        );

        if (findUser.getDeletedAt() != null) {
            throw new ApplicationException(ErrorCode.ALREADY_DELETED_USER);
        }

        if (!passwordEncoder.matches(request.getPassword(), findUser.getPassword())) {
            throw new ApplicationException(ErrorCode.INCORRECT_PASSWORD);
        }

        // Access & Refresh Token 생성
        String accessToken = jwtUtil.createAccessToken(findUser.getUserId(), findUser.getUserType());
        String refreshToken = jwtUtil.createRefreshToken(findUser.getUserId());

        // RefreshToken 저장 및 업데이트
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

        return new TokenResponse(accessToken, createRefreshTokenCookie(refreshToken, REFRESH_TOKEN_EXPIRY_DAY));
    }

    @Transactional
    public ResponseCookie logout(AuthUser authUser) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);
        refreshTokenRepository.findByUser(user)
            .ifPresent(refreshTokenRepository::delete);

        return createRefreshTokenCookie("", 0);
    }

    @Transactional
    public ResponseCookie withdraw(AuthUser authUser, WithDrawRequest request, LocalDateTime deletedAt) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApplicationException(ErrorCode.INCORRECT_PASSWORD);
        }

        if (user.getDeletedAt() != null) {
            throw new ApplicationException(ErrorCode.ALREADY_DELETED_USER);
        }

        user.deleteUser(deletedAt);

        List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(user.getUserId());
        userAddressRepository.deleteAllInBatch(userAddressList);

        refreshTokenRepository.findByUser(user)
            .ifPresent(refreshTokenRepository::delete);

        return createRefreshTokenCookie("", 0);
    }

    @Transactional
    public AccessTokenResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank() || jwtUtil.isTokenExpired(refreshToken)) {
            throw new ApplicationException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        RefreshToken savedToken = refreshTokenRepository.findByRefreshTokenOrElseThrow(refreshToken);

        User user = savedToken.getUser();
        String newAccessToken = jwtUtil.createAccessToken(user.getUserId(), user.getUserType());

        return new AccessTokenResponse(newAccessToken);
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken, long maxAgeSeconds) {
        return ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)                             // JavaScript 에서 접근 불가 (XSS 공격 방지)
            .secure(true)                               // HTTPS 환경에서만 사용 가능
            .sameSite("Strict")                         // CSRF 공격 방지
            .maxAge(maxAgeSeconds)                      // 유지 시간
            .path("/")                                  // 모든 경로에서 쿠키 접근 가능
            .build();
    }
}