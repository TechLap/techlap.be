package com.example.techlap.domain.respond.DTO;

import com.example.techlap.domain.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResCustomerLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private CustomerLogin customer;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerLogin {
        private long id;
        private String email;
        private String fullName;
        private long totalCart;
        private Role role;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerGetAccount {
        private CustomerLogin customer;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerInsideToken {
        private long id;
        private String email;
        private String fullName;
    }
}
