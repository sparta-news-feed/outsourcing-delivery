package com.outsourcingdelivery.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsourcingdelivery.common.auth.JwtUtil;
import com.outsourcingdelivery.common.config.PasswordEncoder;
import com.outsourcingdelivery.domain.auth.controller.AuthController;
import com.outsourcingdelivery.domain.auth.service.AuthService;
import com.outsourcingdelivery.domain.review.controller.ReviewController;
import com.outsourcingdelivery.domain.user.controller.UserAddressController;
import com.outsourcingdelivery.domain.user.controller.UserController;
import com.outsourcingdelivery.domain.user.enums.UserType;
import com.outsourcingdelivery.domain.user.service.UserAddressService;
import com.outsourcingdelivery.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    AuthController.class,
    UserController.class,
    UserAddressController.class,
    ReviewController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected UserAddressService userAddressService;

    protected String accessToken;

    @BeforeEach
    void setUp() {
        accessToken = jwtUtil.createAccessToken(1L, UserType.OWNER);
    }

}
