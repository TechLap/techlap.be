package com.example.techlap.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class JwtConstants {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${techlap.jwt.base64-secret}")
    public String jwtKey;

    @Value("${techlap.jwt.access-token-validity-in-seconds}")
    public long accessTokenExpiration;

    @Value("${techlap.jwt.refresh-token-validity-in-seconds}")
    public long refreshTokenExpiration;

}