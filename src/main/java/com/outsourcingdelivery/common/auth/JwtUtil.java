package com.outsourcingdelivery.common.auth;

import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.user.enums.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Getter
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000L;        // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;   // 7일

    @Value("${jwt.secret.key}")
    private String secretKey;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public JwtUtil() {

    }

    public JwtUtil(String secretKey) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new ApplicationException(ErrorCode.INVALID_JWT_SECRET);
        }
        this.secretKey = secretKey;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, UserType userType) {
        Map<String, String> claims = Map.of(
//            "email", email,
            "userType", userType.name()
        );

        return BEARER_PREFIX + createToken(userId, claims, ACCESS_TOKEN_EXPIRATION);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, Map.of("timestamp", String.valueOf(Instant.now())), REFRESH_TOKEN_EXPIRATION);
    }

    private String createToken(Long userId, Map<String, String> claims, long expiration) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expiration, ChronoUnit.MILLIS);

        return Jwts.builder()
            .subject(userId.toString())
            .claims(claims)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(key)
            .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }

        throw new ApplicationException(ErrorCode.MISSING_JWT_TOKEN);
    }
}
