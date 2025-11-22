package com.example.demo;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<FirebaseTokenFilter> firebaseFilter() {
        FilterRegistrationBean<FirebaseTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FirebaseTokenFilter());
        registrationBean.addUrlPatterns("/api/*"); // protect all /api routes
        return registrationBean;
    }
}
