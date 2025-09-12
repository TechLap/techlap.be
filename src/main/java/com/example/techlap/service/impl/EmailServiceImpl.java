package com.example.techlap.service.impl;

import java.util.Locale;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;

import com.example.techlap.domain.PasswordResetToken;
import com.example.techlap.domain.User;
import com.example.techlap.repository.PasswordResetTokenRepository;
import com.example.techlap.service.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
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
    public void send(SimpleMailMessage email) {
        mailSender.send(email); // ✅ Gửi thật sự qua SMTP
        System.out.println("✅ Email đã được gửi tới: " + email.getTo()[0]);
    }

    @Override
    public SimpleMailMessage constructResetTokenEmail(
            String contextPath, Locale locale, String token, User user) {
        String url = contextPath + "/user/change-password?token=" + token;
        String message = messages.getMessage("message.resetPassword",
                null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
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

}
