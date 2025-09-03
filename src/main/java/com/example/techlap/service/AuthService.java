package com.example.techlap.service;

import com.example.techlap.domain.Customer;
import com.example.techlap.domain.request.ReqLoginDTO;
import com.example.techlap.domain.respond.DTO.ResLoginDTO;

public interface AuthService {

    ResLoginDTO login(ReqLoginDTO loginDTO) throws Exception;

    Customer register(Customer customer) throws Exception;

    ResLoginDTO getRefreshToken(String refresh_token) throws Exception;

    ResLoginDTO.UserGetAccount getAccount() throws Exception;

    Void logout() throws Exception;
}
