package com.outsourcingdelivery.domain.review.repository;

import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.repository.BaseRepository;
import com.outsourcingdelivery.domain.review.entity.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends BaseRepository<Review, Long>, ReviewRepositoryQueryDsl {

    @Query("select r from Review r join fetch r.user u where u.userId = :userId")
    Optional<Review> findByUserId(@Param("userId") Long userId);

}
