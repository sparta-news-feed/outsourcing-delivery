package com.outsourcingdelivery.domain.review.service;

import com.outsourcingdelivery.common.config.PasswordEncoder;
import com.outsourcingdelivery.common.config.TestConfig;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.SpringBootTestSupport;
import com.outsourcingdelivery.domain.menu.entity.Menu;
import com.outsourcingdelivery.domain.menu.repository.MenuRepository;
import com.outsourcingdelivery.domain.order.entity.Order;
import com.outsourcingdelivery.domain.order.enums.OrderStatus;
import com.outsourcingdelivery.domain.order.repository.OrderRepository;
import com.outsourcingdelivery.domain.review.dto.request.CreateReviewRequest;
import com.outsourcingdelivery.domain.review.dto.request.UpdateReviewRequest;
import com.outsourcingdelivery.domain.review.dto.response.ReviewResponse;
import com.outsourcingdelivery.domain.review.entity.Review;
import com.outsourcingdelivery.domain.review.repository.ReviewRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.enums.UserType;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class ReviewServiceTest extends SpringBootTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    ReviewService reviewService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private User user;
    private Store store;
    private Menu menu;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email("abc@abc.com")
            .password(passwordEncoder.encode("Password1234!"))
            .userType(UserType.OWNER)
            .phoneNumber("01012345678")
            .username("홍길동")
            .build();

        store = Store.builder()
            .storeName("가게")
            .minOrderPrice(10000)
            .phoneNumber("01012345678")
            .address("서울")
            .user(user)
            .build();

        menu = Menu.builder()
            .store(store)
            .description("메뉴 설명")
            .price(10000)
            .menuName("메뉴 이름")
            .build();


        order = Order.builder()
            .amount(1)
            .orderStatus(OrderStatus.DELIVERED)
            .user(user)
            .menu(menu)
            .build();
    }

    @DisplayName("리뷰가 정상적으로 생성된다.")
    @Test
    void createReview1() throws Exception {
        // given
        User saveUser = userRepository.save(user);
        Store saveStore = storeRepository.save(store);
        Menu saveMenu = menuRepository.save(menu);
        Order saveOrder = orderRepository.save(order);

        AuthUser authUser = AuthUser.builder()
            .userId(saveUser.getUserId())
            .build();

        CreateReviewRequest request = CreateReviewRequest.builder()
            .contents("리뷰")
            .rating((short) 5)
            .storeId(saveStore.getStoreId())
            .orderNo(saveOrder.getOrderNo())
            .build();

        // when
        reviewService.createReview(authUser, request);
        Review review = reviewRepository.findByIdOrElseThrow(1L, ErrorCode.NOT_FOUND_REVIEW);

        // then
        assertThat(review)
            .extracting("reviewId", "contents", "rating", "store", "order")
            .containsExactly(1L, "리뷰", (short) 5, saveStore, saveOrder);
    }

    @DisplayName("리뷰 생성중에 주문 상태가 '배달완료(DELIVERED)'가 아니라면 예외가 발생한다.")
    @Test
    void createReview2() throws Exception {
        // given
        User saveUser = userRepository.save(user);
        Store saveStore = storeRepository.save(store);

        Order order = Order.builder()
            .amount(1)
            .orderStatus(OrderStatus.ORDERED)
            .user(user)
            .build();

        Order saveOrder = orderRepository.save(order);

        AuthUser authUser = AuthUser.builder()
            .userId(saveUser.getUserId())
            .build();

        CreateReviewRequest request = CreateReviewRequest.builder()
            .contents("리뷰")
            .rating((short) 5)
            .storeId(saveStore.getStoreId())
            .orderNo(saveOrder.getOrderNo())
            .build();

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(authUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.FORBIDDEN_REVIEW_CREATION.getMessage());
    }

    @DisplayName("가게의 리뷰 전체 조회가 성공적으로 조회된다.")
    @Test
    void getAllReviews1() throws Exception {
        // given
        User savedUser = userRepository.save(user);
        Store savedStore = storeRepository.save(store);
        Menu saveMenu = menuRepository.save(menu);

        Order savedOrder1 = orderRepository.save(createOrder(2));
        Thread.sleep(10);
        Order savedOrder2 = orderRepository.save(createOrder(3));
        Thread.sleep(10);
        Order savedOrder3 = orderRepository.save(createOrder(4));

        List<Review> reviews = List.of(
            createReview("댓글 1", 1, savedUser, savedStore, savedOrder1),
            createReview("댓글 2", 2, savedUser, savedStore, savedOrder2),
            createReview("댓글 3", 3, savedUser, savedStore, savedOrder3)
        );
        reviewRepository.saveAll(reviews);

        // when
        PageResponse<ReviewResponse> response = reviewService.getAllReviews(savedStore.getStoreId(), 1, 10, null, null);

        // then
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getContent())
            .extracting("contents")
            .containsExactlyInAnyOrder("댓글 3", "댓글 2", "댓글 1");
        assertThat(response.getContent())
            .extracting("rating")
            .containsExactlyInAnyOrder((short) 3, (short) 2, (short) 1);
    }

    @DisplayName("가게의 리뷰 전체 조회시 평점이 3이상인 리뷰만 조회된다.")
    @Test
    void getAllReviews2() throws Exception {
        // given
        User savedUser = userRepository.save(user);
        Store savedStore = storeRepository.save(store);
        Menu saveMenu = menuRepository.save(menu);

        Order savedOrder1 = orderRepository.save(createOrder(2));
        Thread.sleep(10);
        Order savedOrder2 = orderRepository.save(createOrder(3));
        Thread.sleep(10);
        Order savedOrder3 = orderRepository.save(createOrder(4));

        List<Review> reviews = List.of(
            createReview("댓글 1", 5, savedUser, savedStore, savedOrder1),
            createReview("댓글 2", 1, savedUser, savedStore, savedOrder2),
            createReview("댓글 3", 3, savedUser, savedStore, savedOrder3)
        );
        reviewRepository.saveAll(reviews);

        // when
        PageResponse<ReviewResponse> response = reviewService.getAllReviews(savedStore.getStoreId(), 1, 10, 3, null);

        // then
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getContent())
            .extracting("contents")
            .containsExactlyInAnyOrder("댓글 1", "댓글 3");
        assertThat(response.getContent())
            .extracting("rating")
            .containsExactlyInAnyOrder((short) 3, (short) 5);
    }

    @DisplayName("가게의 리뷰 전체 조회시 사이즈를 1로 설정하여 가져온다.")
    @Test
    void getAllReviews3() throws Exception {
        // given
        User savedUser = userRepository.save(user);
        Store savedStore = storeRepository.save(store);
        Menu saveMenu = menuRepository.save(menu);

        Order savedOrder1 = orderRepository.save(createOrder(2));
        Thread.sleep(10);
        Order savedOrder2 = orderRepository.save(createOrder(3));
        Thread.sleep(10);
        Order savedOrder3 = orderRepository.save(createOrder(4));

        List<Review> reviews = List.of(
            createReview("댓글 1", 1, savedUser, savedStore, savedOrder1),
            createReview("댓글 2", 2, savedUser, savedStore, savedOrder2),
            createReview("댓글 3", 3, savedUser, savedStore, savedOrder3)
        );
        reviewRepository.saveAll(reviews);

        // when
        PageResponse<ReviewResponse> response = reviewService.getAllReviews(savedStore.getStoreId(), 1, 1, null, null);

        // then
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent())
            .extracting("contents")
            .containsExactlyInAnyOrder("댓글 1");
        assertThat(response.getContent())
            .extracting("rating")
            .containsExactlyInAnyOrder((short) 1);
    }

    @DisplayName("리뷰 업데이트 요청시 성공적으로 업데이트된다.")
    @Test
    void updateReview1() throws Exception {
        // given
        User savedUser = userRepository.save(user);
        Store savedStore = storeRepository.save(store);
        Menu saveMenu = menuRepository.save(menu);
        Order savedOrder = orderRepository.save(order);

        Review review = createReview("댓글", 1, savedUser, savedStore, savedOrder);
        reviewRepository.save(review);

        AuthUser authUser = AuthUser.builder()
            .userId(savedUser.getUserId())
            .userType(savedUser.getUserType())
            .build();

        UpdateReviewRequest request = UpdateReviewRequest.builder()
            .contents("수정")
            .rating((short) 5)
            .build();

        reviewService.updateReview(authUser, review.getReviewId(), request, LocalDateTime.now());

        // when
        Review updateReview = reviewRepository.findByIdOrElseThrow(review.getReviewId(), ErrorCode.NOT_FOUND_REVIEW);

        // then
        assertThat(review.getReviewId()).isEqualTo(updateReview.getReviewId());
        assertThat(updateReview)
            .extracting("contents", "rating")
            .containsExactly("수정", (short) 5);
    }

    @DisplayName("리뷰 업데이트 요청시 리뷰가 존재하지 않으면 예외가 발생한다.")
    @Test
    void updateReview2() throws Exception {
        // given
        User savedUser = userRepository.save(user);
        AuthUser authUser = AuthUser.builder()
            .userId(savedUser.getUserId())
            .userType(savedUser.getUserType())
            .build();

        UpdateReviewRequest request = UpdateReviewRequest.builder()
            .contents("수정")
            .rating((short) 5)
            .build();

        // when & then
        assertThatThrownBy(() -> reviewService.updateReview(authUser, 1L, request, LocalDateTime.now()))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_REVIEW.getMessage() + " id = 1");

    }

    @DisplayName("리뷰 업데이트 요청시 다른 사람의 리뷰 수정시 예외가 발생한다.")
    @Test
    void updateReview3() throws Exception {
        // given
        User savedUser = userRepository.save(user);
        Store savedStore = storeRepository.save(store);
        Menu saveMenu = menuRepository.save(menu);
        Order savedOrder = orderRepository.save(order);

        User otherUser = userRepository.save(
            createUser(
                "def@def.com",
                "Password1234!",
                "홍길동",
                UserType.OWNER,
                "01012345678"
            )
        );

        Review review = createReview("댓글", 1, savedUser, savedStore, savedOrder);
        reviewRepository.save(review);

        AuthUser authUser = AuthUser.builder()
            .userId(otherUser.getUserId())
            .userType(otherUser.getUserType())
            .build();

        UpdateReviewRequest request = UpdateReviewRequest.builder()
            .contents("수정")
            .rating((short) 5)
            .build();

        // when & then
        assertThatThrownBy(() -> reviewService.updateReview(authUser, review.getReviewId(), request, LocalDateTime.now()))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.FORBIDDEN_REVIEW_UPDATE.getMessage());

    }

    @DisplayName("리뷰 업데이트 요청시 작성 후 3일이 지나면 예외가 발생한다.")
    @Test
    void updateReview4() throws Exception {
        // given
        User savedUser = userRepository.save(user);
        Store savedStore = storeRepository.save(store);
        Menu saveMenu = menuRepository.save(menu);
        Order savedOrder = orderRepository.save(order);

        Review review = createReview("댓글", 1, savedUser, savedStore, savedOrder);
        reviewRepository.save(review);

        AuthUser authUser = AuthUser.builder()
            .userId(savedUser.getUserId())
            .userType(savedUser.getUserType())
            .build();

        UpdateReviewRequest request = UpdateReviewRequest.builder()
            .contents("수정")
            .rating((short) 5)
            .build();

        LocalDateTime now = LocalDateTime.now().plusDays(4);

        // when & then
        assertThatThrownBy(() -> reviewService.updateReview(authUser, review.getReviewId(), request, now))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.FORBIDDEN_REVIEW_EDIT_EXPIRED.getMessage());

    }

    @DisplayName("리뷰 삭제시 정상적으로 삭제된다.")
    @Test
    void deleteReview1() throws Exception {
        // given
        User savedUser = userRepository.save(user);
        Store savedStore = storeRepository.save(store);
        Menu saveMenu = menuRepository.save(menu);
        Order savedOrder = orderRepository.save(order);

        Review review = createReview("댓글", 1, savedUser, savedStore, savedOrder);
        Review savedReview = reviewRepository.save(review);

        Long saveId = savedReview.getReviewId();

        AuthUser authUser = AuthUser.builder()
            .userId(savedUser.getUserId())
            .userType(savedUser.getUserType())
            .build();

        // when
        reviewService.deleteReview(authUser, savedReview.getReviewId());

        // then
        assertThatThrownBy(() -> reviewRepository.findByIdOrElseThrow(savedReview.getReviewId(), ErrorCode.NOT_FOUND_REVIEW))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_REVIEW.getMessage() + " id = " + saveId);
    }

    @DisplayName("리뷰 삭제 요청시 자신의 리뷰가 아닐시 예외가 발생한다.")
    @Test
    void deleteReview2() throws Exception {
        // given
        User savedUser = userRepository.save(user);
        Store savedStore = storeRepository.save(store);
        Menu saveMenu = menuRepository.save(menu);
        Order savedOrder = orderRepository.save(order);

        Review review = createReview("댓글", 1, savedUser, savedStore, savedOrder);
        Review savedReview = reviewRepository.save(review);

        AuthUser authUser = AuthUser.builder()
            .userId(-1L)
            .userType(UserType.OWNER)
            .build();

        // when & then
        assertThatThrownBy(() -> reviewService.deleteReview(authUser, savedReview.getReviewId())
        )
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.FORBIDDEN_REVIEW_DELETE.getMessage());
    }

    private User createUser(String email, String password, String username, UserType userType, String phoneNumber) {
        return User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .username(username)
            .userType(userType)
            .phoneNumber(phoneNumber)
            .build();
    }

    private Review createReview(String contents, int rating, User user, Store store, Order order) {
        return Review.builder()
            .contents(contents)
            .rating((short) rating)
            .user(user)
            .store(store)
            .order(order)
            .build();
    }

    private Order createOrder(int amount) {
        return Order.builder()
            .amount(amount)
            .orderStatus(OrderStatus.DELIVERED)
            .user(user)
            .menu(menu)
            .build();
    }

}