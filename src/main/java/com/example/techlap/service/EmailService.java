package com.example.techlap.service;

import java.util.Locale;

import org.springframework.mail.SimpleMailMessage;

import com.example.techlap.domain.User;

import jakarta.servlet.http.HttpServletRequest;

public interface EmailService {

    void createPasswordResetTokenForUser(User user, String token);

    void send(SimpleMailMessage email);

    String getAppUrl(HttpServletRequest request);

    SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, User user);
}