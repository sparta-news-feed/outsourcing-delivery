package com.outsourcingdelivery.domain.user.repository;

import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.common.repository.BaseRepository;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.enums.UserType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    Optional<User> findUserByEmailAndUserType(String email, UserType userType);

    default User findUserByEmailAndUserTypeOrElseThrow(String email, UserType userType) {
        return findUserByEmailAndUserType(email, userType).orElseThrow(
            () -> new ApplicationException(ErrorCode.NOT_FOUND_USER)
        );
    }

    boolean existsByEmailAndUserType(String email, UserType userType);

}
