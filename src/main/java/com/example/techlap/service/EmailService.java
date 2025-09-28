package com.example.techlap.service;

import java.util.Locale;

import org.springframework.mail.SimpleMailMessage;

import com.example.techlap.domain.User;
import com.example.techlap.domain.Customer;
import com.example.techlap.domain.Order;
import com.example.techlap.domain.respond.GenericResponse;
import com.example.techlap.domain.request.ReqPasswordTokenDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface EmailService {

    void createPasswordResetTokenForUser(User user, String token);

    void createPasswordResetTokenForCustomer(Customer customer, String token);

    void send(SimpleMailMessage email);

    void sendInvoiceEmail(Order order);

    String getAppUrl(HttpServletRequest request);

    SimpleMailMessage constructResetTokenEmailUser(String contextPath, Locale locale, String token, User user);

    SimpleMailMessage constructResetTokenEmailCustomer(String contextPath, Locale locale, String token,
            Customer customer);
    GenericResponse resetUserPassword(HttpServletRequest request,
            String email) throws Exception;

    GenericResponse saveUserPassword(Locale locale, ReqPasswordTokenDTO reqPasswordDTO) throws Exception;

    GenericResponse resetCustomerPassword(HttpServletRequest request,
            String email) throws Exception;

    GenericResponse saveCustomerPassword(Locale locale, ReqPasswordTokenDTO reqPasswordDTO) throws Exception;

}