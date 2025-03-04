package com.outsourcingdelivery.domain.auth.repository;

import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.auth.entity.RefreshToken;
import com.outsourcingdelivery.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);

    @Query("select r from RefreshToken r join fetch r.user where r.refreshToken = :refreshToken")
    Optional<RefreshToken> findByRefreshToken(@Param("refreshToken") String refreshToken);

    default RefreshToken findByRefreshTokenOrElseThrow(String refreshToken) {
        return findByRefreshToken(refreshToken).orElseThrow(
            () -> new ApplicationException(ErrorCode.NOT_FOUND_TOKEN)
        );
    }

}
