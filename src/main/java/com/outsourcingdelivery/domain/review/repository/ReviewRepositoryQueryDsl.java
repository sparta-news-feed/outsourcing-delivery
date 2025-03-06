package com.outsourcingdelivery.domain.review.repository;

import com.outsourcingdelivery.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReviewRepositoryQueryDsl {

    Page<Review> findAllByStoreId(Long storeId, Integer ratingStart, Integer ratingEnd, Pageable pageable);

}
