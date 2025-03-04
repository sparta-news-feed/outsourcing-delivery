package com.outsourcingdelivery.domain.review.repository;

import com.outsourcingdelivery.common.repository.BaseRepository;
import com.outsourcingdelivery.domain.review.dto.response.ReviewResponse;
import com.outsourcingdelivery.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends BaseRepository<Review, Long> {

    @Query("select r from Review r where r.storeId = :storeId")
//    @Query("select r from Review r join fetch r.storeId where r.storeId = :stordId")
    Page<Review> findAllByStoreId(@Param("storeId") Long storeId, Pageable pageable);

}
