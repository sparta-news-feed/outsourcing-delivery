package com.outsourcingdelivery.domain.review.repository;

import com.outsourcingdelivery.domain.review.dto.response.ReviewResponse;
import com.outsourcingdelivery.domain.review.entity.QReview;
import com.outsourcingdelivery.domain.review.entity.Review;
import com.outsourcingdelivery.domain.store.entity.QStore;
import com.outsourcingdelivery.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewRepositoryQueryDslImpl implements ReviewRepositoryQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Review> findAllByStoreId(Long storeId, Integer ratingStart, Integer ratingEnd, Pageable pageable) {

        QReview review = QReview.review;
        QStore store = QStore.store;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(review.store.storeId.eq(storeId));

        if (ratingStart != null) {
            builder.and(review.rating.goe(ratingStart));
        }

        if (ratingEnd != null) {
            builder.and(review.rating.loe(ratingEnd));
        }

        List<Review> reviewResponse = queryFactory
            .select(review)
            .from(review)
            .join(review.store, store)
            .join(review.user, user)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = Optional.ofNullable(queryFactory
                .select(review.count())
                .from(review)
                .where(builder)
                .fetchOne())
            .orElse(0L);

        return new PageImpl<>(reviewResponse, pageable, total);
    }
}
