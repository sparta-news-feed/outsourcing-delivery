package com.outsourcingdelivery.domain.review.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.order.entity.Order;
import com.outsourcingdelivery.domain.order.repository.OrderRepository;
import com.outsourcingdelivery.domain.review.dto.request.CreateReviewRequest;
import com.outsourcingdelivery.domain.review.dto.request.UpdateReviewRequest;
import com.outsourcingdelivery.domain.review.dto.response.ReviewResponse;
import com.outsourcingdelivery.domain.review.entity.Review;
import com.outsourcingdelivery.domain.review.repository.ReviewRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void createReview(AuthUser authUser, @Valid CreateReviewRequest request) {

        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);

        Store findStore = storeRepository.findByIdOrElseThrow(request.getStoreId(), ErrorCode.NOT_FOUND_STORE);
        Order findOrder = orderRepository.findByIdOrElseThrow(request.getOrderNo(), ErrorCode.NOT_FOUND_ORDER);

//        if (!findOrder.getOrderStatus().equals(OrderStatus.DELIVERED)) {
//            throw new ApplicationException(ErrorCode.REVIEW_CREATION_FORBIDDEN);
//        }

        Review review = Review.builder()
            .contents(request.getContents())
            .rating(request.getRating())
            .user(user)
            .order(findOrder)
            .store(findStore)
            .build();

        reviewRepository.save(review);
    }

    public PageResponse<ReviewResponse> getAllReviews(Long storeId, int page, int size, Integer ratingStart, Integer ratingEnd) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by("createdAt").descending());
        Page<ReviewResponse> results = reviewRepository.findAllByStoreId(
                storeId,
                ratingStart,
                ratingEnd,
                pageable)
            .map(ReviewResponse::toDto);

        return PageResponse.toDto(results);
    }

    @Transactional
    public void updateReview(AuthUser authUser, Long reviewId, @Valid UpdateReviewRequest request) {
        Review review = reviewRepository.findByIdOrElseThrow(reviewId, ErrorCode.REVIEW_NOT_FOUND);

        if (!authUser.getUserId().equals(review.getUser().getUserId())) {
            throw new ApplicationException(ErrorCode.REVIEW_EDIT_FORBIDDEN);
        }

        LocalDateTime now = LocalDateTime.now();
        if (ChronoUnit.DAYS.between(review.getCreatedAt(), now) > 3) {
            throw new ApplicationException(ErrorCode.REVIEW_EDIT_EXPIRED);
        }

        review.updateReview(request.getContents(), request.getRating());
    }

    @Transactional
    public void deleteReview(AuthUser authUser, Long reviewId) {
        Review review = reviewRepository.findByIdOrElseThrow(reviewId, ErrorCode.REVIEW_NOT_FOUND);

        if (!authUser.getUserId().equals(review.getUser().getUserId())) {
            throw new ApplicationException(ErrorCode.REVIEW_EDIT_FORBIDDEN);
        }

        reviewRepository.delete(review);
    }
}
