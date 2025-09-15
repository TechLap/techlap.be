package com.example.techlap.service.impl;

import java.util.Locale;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;

import com.example.techlap.domain.Customer;
import com.example.techlap.domain.PasswordResetToken;
import com.example.techlap.domain.User;
import com.example.techlap.repository.PasswordResetTokenRepository;
import com.example.techlap.service.EmailService;
import com.example.techlap.service.UserService;
import com.example.techlap.service.CustomerService;
import com.example.techlap.util.SecurityUtil;
import com.example.techlap.domain.respond.GenericResponse;
import com.example.techlap.domain.request.ReqPasswordTokenDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserService userService;
    private final CustomerService customerService;
    private final SecurityUtil securityUtil;
    private final Environment env;
    private final MessageSource messages;
    private final JavaMailSender mailSender;

    @Override
    @Transactional
    public void createPasswordResetTokenForUser(User user, String token) {
        passwordResetTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.flush();
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    @Transactional
    public void createPasswordResetTokenForCustomer(Customer customer, String token) {
        passwordResetTokenRepository.deleteByCustomer(customer);
        passwordResetTokenRepository.flush();
        PasswordResetToken myToken = new PasswordResetToken(token, customer);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public void send(SimpleMailMessage email) {
        mailSender.send(email); // Gửi email qua SMTP server
        System.out.println("✅ Email đã được gửi tới: " + email.getTo()[0]);
    }

    @Override
    public SimpleMailMessage constructResetTokenEmailUser(
            String contextPath, Locale locale, String token, User user) {
        String url = contextPath + "/user/change-password?token=" + token;
        String message = messages.getMessage("message.resetPassword",
                null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    @Override
    public SimpleMailMessage constructResetTokenEmailCustomer(
            String contextPath, Locale locale, String token, Customer customer) {
        String url = contextPath + "/customer/change-password?token=" + token;
        String message = messages.getMessage("message.resetPassword",
                null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, customer);
    }

    private SimpleMailMessage constructEmail(String subject, String body,
            Customer customer) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(customer.getEmail());
        email.setFrom(env.getProperty("spring.mail.username"));
        return email;
    }

    private SimpleMailMessage constructEmail(String subject, String body,
            User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("spring.mail.username"));
        return email;
    }

    public String getAppUrl(HttpServletRequest request) {
        String scheme = request.getScheme(); // http hoặc https
        String serverName = request.getServerName(); // domain hoặc localhost
        int serverPort = request.getServerPort(); // 8080, 80, 443...
        String contextPath = request.getContextPath(); // nếu app có context

        if ((scheme.equals("http") && serverPort == 80) ||
                (scheme.equals("https") && serverPort == 443)) {
            return scheme + "://" + serverName + contextPath;
        } else {
            return scheme + "://" + serverName + ":" + serverPort + contextPath;
        }
    }

    @Override
    @Transactional
    public GenericResponse resetUserPassword(HttpServletRequest request, String email) throws Exception {
        User user = userService.fetchUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);
        send(constructResetTokenEmailUser(getAppUrl(request),
                request.getLocale(), token, user));
        return new GenericResponse(
                messages.getMessage("message.resetPasswordEmail", null,
                        request.getLocale()));
    }

    @Override
    @Transactional
    public GenericResponse saveUserPassword(Locale locale, ReqPasswordTokenDTO reqPasswordDTO) throws Exception {
        String result = securityUtil.validatePasswordResetToken(reqPasswordDTO.getToken());
        if (result != null) {
            return new GenericResponse(messages.getMessage("auth.message." + result, null, locale));
        }
        User user = userService.getUserByPasswordResetToken(reqPasswordDTO.getToken());
        if (user != null) {
            userService.changeUserPassword(user, reqPasswordDTO.getNewPassword());
            return new GenericResponse(messages.getMessage("message.resetPasswordSuccess", null, locale));
        } else {
            return new GenericResponse(messages.getMessage("auth.message.invalid", null, locale));
        }
    }

    @Override
    @Transactional
    public GenericResponse resetCustomerPassword(HttpServletRequest request, String email) throws Exception {
        Customer customer = customerService.fetchCustomerByEmail(email);
        if (customer == null) {
            throw new UsernameNotFoundException(email);
        }
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForCustomer(customer, token);
        send(constructResetTokenEmailCustomer(getAppUrl(request),
                request.getLocale(), token, customer));
        return new GenericResponse(
                messages.getMessage("message.resetPasswordEmail", null,
                        request.getLocale()));
    }

    @Override
    @Transactional
    public GenericResponse saveCustomerPassword(Locale locale, ReqPasswordTokenDTO reqPasswordDTO) throws Exception {
        String result = securityUtil.validatePasswordResetToken(reqPasswordDTO.getToken());
        if (result != null) {
            return new GenericResponse(messages.getMessage("auth.message." + result, null, locale));
        }
        Customer customer = customerService.getCustomerByPasswordResetToken(reqPasswordDTO.getToken());
        if (customer != null) {
            customerService.changeCustomerPassword(customer, reqPasswordDTO.getNewPassword());
            return new GenericResponse(messages.getMessage("message.resetPasswordSuccess", null, locale));
        } else {
            return new GenericResponse(messages.getMessage("auth.message.invalid", null, locale));
        }
    }

}
