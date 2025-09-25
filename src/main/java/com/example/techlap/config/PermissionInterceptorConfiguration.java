package com.example.techlap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    @Bean
    public PermissionInterceptor permissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/",
                "/api/v1/login", "/api/v1/login/**",
                "/api/v1/admin/login", "/api/v1/admin/login/**",
                "/api/v1/register", "/api/v1/register/**",
                "/api/v1/auth/refresh",
                "/api/v1/auth/customers/refresh",
                "/api/v1/auth/logout",
                "/api/v1/auth/customers/logout",
                "/api/v1/auth/customers/account",
                "/api/v1/auth/account",
                "/api/v1/users/change-password",
                "/api/v1/users/me/change-password",
                "/api/v1/users/save-password",
                "/api/v1/users/dashboard",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/actuator/health",
                "/api/v1/users/reset-password",
                "/api/v1/customers/reset-password",
                "/api/v1/customers/change-password",
                "/api/v1/customers/me/change-password",
                "/api/v1/products/**",
                "/api/v1/categories",
                "/api/v1/categories/filter",
                "/api/v1/brands",
                "/api/v1/brands/filter",
                "/api/v1/products",
                "/api/v1/products/best-sellers",
                "/api/v1/products/latest",
                "/api/v1/products/filter",
                "/api/v1/permissions",
                "/api/v1/permissions/filter",
                "/api/v1/payment/vnpay-callback",
                "/api/v1/payment/vnpay-verify",
                "/api/v1/customers/add-to-cart",
                "/api/v1/customers/get-cart",
                "/api/v1/customers/remove-cart-detail",
                "/api/v1/customers/history-orders",
                "/api/v1/files/**",
                "/storage/**",
                "/api/v1/orders",
                "/api/v1/orders/filter",
                "/api/v1/orders/code/**",
                "/error",
        };
        registry.addInterceptor(permissionInterceptor()).excludePathPatterns(whiteList);
    }
}