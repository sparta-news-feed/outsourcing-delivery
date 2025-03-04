package com.outsourcingdelivery.domain.review.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.review.dto.request.CreateReviewRequest;
import com.outsourcingdelivery.domain.review.dto.request.UpdateReviewRequest;
import com.outsourcingdelivery.domain.review.dto.response.ReviewResponse;
import com.outsourcingdelivery.domain.review.entity.Review;
import com.outsourcingdelivery.domain.review.mock.Order;
import com.outsourcingdelivery.domain.review.mock.Store;
import com.outsourcingdelivery.domain.review.repository.ReviewRepository;
import com.outsourcingdelivery.domain.review.repository.mock.MockRepository;
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

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    @Transactional
    public void createReview(AuthUser authUser, @Valid CreateReviewRequest request) {

        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.USER_NOT_FOUND);

        Order findOrder = MockRepository.orderRepository
            .stream()
            .filter(order -> order.getOrderId().equals(request.getOrderId()))
            .findFirst()
            .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Store findStore = MockRepository.storeRepository
            .stream()
            .filter(store -> store.getStoreId().equals(request.getStoreId()))
            .findFirst()
            .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Review review = Review.builder()
            .contents(request.getContents())
            .rating(request.getRating())
            .user(user)
            .orderId(findOrder.getOrderId())
            .storeId(findStore.getStoreId())
            .build();

        reviewRepository.save(review);
    }

    public PageResponse<ReviewResponse> getAllReviews(Long storeId, int page, int size, Integer ratingStart, Integer ratingEnd) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by("createdAt").descending());
        Page<ReviewResponse> results = reviewRepository.findAllByStoreId(storeId, pageable)
            .map(ReviewResponse::toDto);

        return PageResponse.toDto(results);
    }

    @Transactional
    public void updateReview(Long reviewId, @Valid UpdateReviewRequest request) {
        Review findReview = reviewRepository.findByIdOrElseThrow(reviewId, ErrorCode.REVIEW_NOT_FOUND);
        findReview.updateReview(request.getContents(), request.getRating());
    }
}
