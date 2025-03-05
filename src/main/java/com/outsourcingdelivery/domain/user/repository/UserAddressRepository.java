package com.outsourcingdelivery.domain.user.repository;

import com.outsourcingdelivery.common.repository.BaseRepository;
import com.outsourcingdelivery.domain.user.entity.UserAddress;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends BaseRepository<UserAddress, Long> {

    @Query("select ua from UserAddress ua join fetch ua.user where ua.user.userId = :userId order by ua.createdAt")
    List<UserAddress> findAllByUserId(@Param("userId") Long userId);

    @Query("select ua from UserAddress ua join fetch ua.user where ua.userAddressId = :addressId order by ua.createdAt")
    Optional<UserAddress> findByIdWithUser(@Param("addressId") Long addressId);
}
