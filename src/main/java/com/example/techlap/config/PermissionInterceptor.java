package com.example.techlap.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import org.springframework.security.access.AccessDeniedException;
import com.example.techlap.service.AuthService;
import com.example.techlap.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@RequiredArgsConstructor
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        String apiPath = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String methodType = request.getMethod();
        log.info("path: {}", apiPath);
        log.info("requestURI: {}", requestURI);
        log.info("methodType: {}", methodType);

        if (SecurityUtil
                .getCurrentUserLogin()
                .map(email -> authService.hasPermission(email, apiPath, methodType)).orElse(false)){
            return true;
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }
}
