package com.example.techlap.repository;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.techlap.domain.PasswordResetToken;
import com.example.techlap.domain.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    void deleteByUser(User user);

    void deleteByExpiryDateLessThan(Date now);

    PasswordResetToken findByToken(String token);
}
