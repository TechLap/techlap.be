package com.example.techlap.util;

import com.example.techlap.constant.JwtConstants;
import com.example.techlap.domain.respond.DTO.ResCustomerLoginDTO;
import com.example.techlap.domain.respond.DTO.ResLoginDTO;
import com.nimbusds.jose.util.Base64;

import lombok.AllArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SecurityUtil {

    private final JwtEncoder jwtEncoder;
    private final JwtConstants jwtConstants;

    // tạo token khi đăng nhập
    public String createAccessToken(String email, ResLoginDTO dto) {
        ResLoginDTO.UserInsideToken userToken = new ResLoginDTO.UserInsideToken();
        userToken.setId(dto.getUser().getId());
        userToken.setEmail(dto.getUser().getEmail());
        userToken.setFullName(dto.getUser().getFullName());

        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtConstants.getAccessTokenExpiration(), ChronoUnit.SECONDS);

        // hardcode permission (for testing)
        String role = "ROLE_USER";

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userToken)
                .claim("role", role)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JwtConstants.JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,
                claims)).getTokenValue();
    }

    public String createRefreshToken(String email, ResLoginDTO dto) {
        ResLoginDTO.UserInsideToken userToken = new ResLoginDTO.UserInsideToken();
        userToken.setId(dto.getUser().getId());
        userToken.setEmail(dto.getUser().getEmail());
        userToken.setFullName(dto.getUser().getFullName());

        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtConstants.getRefreshTokenExpiration(), ChronoUnit.SECONDS);

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userToken)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JwtConstants.JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,
                claims)).getTokenValue();
    }

    // tạo token khi đăng nhập cho khách hàng
    public String createAccessTokenForCustomer(String email, ResCustomerLoginDTO dto) {
        ResCustomerLoginDTO.CustomerInsideToken customerToken = new ResCustomerLoginDTO.CustomerInsideToken();
        customerToken.setId(dto.getCustomer().getId());
        customerToken.setEmail(dto.getCustomer().getEmail());
        customerToken.setFullName(dto.getCustomer().getFullName());

        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtConstants.getAccessTokenExpiration(), ChronoUnit.SECONDS);

        // hardcode permission (for testing)
        String role = "ROLE_CUSTOMER";

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("customer", customerToken)
                .claim("role", role)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JwtConstants.JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,
                claims)).getTokenValue();
    }

    public String createRefreshTokenForCustomer(String email, ResCustomerLoginDTO dto) {
        ResCustomerLoginDTO.CustomerInsideToken customerToken = new ResCustomerLoginDTO.CustomerInsideToken();
        customerToken.setId(dto.getCustomer().getId());
        customerToken.setEmail(dto.getCustomer().getEmail());
        customerToken.setFullName(dto.getCustomer().getFullName());

        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtConstants.getRefreshTokenExpiration(), ChronoUnit.SECONDS);

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("customer", customerToken)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JwtConstants.JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,
                claims)).getTokenValue();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(this.jwtConstants.getJwtKey()).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JwtConstants.JWT_ALGORITHM.getName());
    }

    public Jwt checkValidRefreshToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(JwtConstants.JWT_ALGORITHM).build();
        try {
            return jwtDecoder.decode(token);

        } catch (Exception e) {
            System.out.println(">>> Refresh Token error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    // public static boolean isAuthenticated() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     return authentication != null && getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
    // }

    /**
     * Checks if the current user has any of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has any of the authorities, false otherwise.
     */
    // public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     return (
    //         authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(authorities).contains(authority))
    //     );
    // }

    /**
     * Checks if the current user has none of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has none of the authorities, false otherwise.
     */
    // public static boolean hasCurrentUserNoneOfAuthorities(String... authorities) {
    //     return !hasCurrentUserAnyOfAuthorities(authorities);
    // }

    /**
     * Checks if the current user has a specific authority.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    // public static boolean hasCurrentUserThisAuthority(String authority) {
    //     return hasCurrentUserAnyOfAuthorities(authority);
    // }

    // private static Stream<String> getAuthorities(Authentication authentication) {
    //     return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
    // }
}