package com.outsourcingdelivery.domain.user.repository;

import com.outsourcingdelivery.domain.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

}
