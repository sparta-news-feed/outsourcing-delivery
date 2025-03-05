package com.outsourcingdelivery.domain.review.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
import com.outsourcingdelivery.domain.order.entity.Order;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private Short rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_no", nullable = false)
    private Order order;

    @Builder
    private Review(Long reviewId, String contents, Short rating, User user, Store store, Order order) {
        this.reviewId = reviewId;
        this.contents = contents;
        this.rating = rating;
        this.user = user;
        this.store = store;
        this.order = order;
    }

    public void updateReview(String contents, Short rating) {
        this.contents = contents;
        this.rating = rating;
    }
}
