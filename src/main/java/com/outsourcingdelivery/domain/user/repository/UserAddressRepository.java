package com.outsourcingdelivery.domain.user.repository;

import com.outsourcingdelivery.common.repository.BaseRepository;
import com.outsourcingdelivery.domain.user.entity.UserAddress;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends BaseRepository<UserAddress, Long> {

}
