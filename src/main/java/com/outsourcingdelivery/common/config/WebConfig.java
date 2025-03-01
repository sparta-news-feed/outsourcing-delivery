package com.outsourcingdelivery.common.config;

import com.outsourcingdelivery.common.jwt.JwtFilter;
import com.outsourcingdelivery.common.interceptor.UserTypeInterceptor;
import com.outsourcingdelivery.common.jwt.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean   // TestCode 를 위해 직접 빈 주입
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean   // 필터
    public FilterRegistrationBean<JwtFilter> jwtFilter() {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtFilter(jwtUtil()));
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserTypeInterceptor())
            .order(1)
            .addPathPatterns("/**")
            .excludePathPatterns("/css/**", "/js/**", "/images/**", "/webjars/**");
    }
}
