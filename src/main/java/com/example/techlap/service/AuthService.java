package com.example.techlap.service;

import com.example.techlap.domain.Customer;
import com.example.techlap.domain.request.ReqLoginDTO;
import com.example.techlap.domain.request.ReqRegisterCustomerDTO;
import com.example.techlap.domain.respond.DTO.ResCustomerLoginDTO;
import com.example.techlap.domain.respond.DTO.ResLoginDTO;
import com.example.techlap.domain.respond.DTO.ResRegisterCustomerDTO;

public interface AuthService {

    ResLoginDTO internalLogin(ReqLoginDTO loginDTO) throws Exception;

    ResCustomerLoginDTO externalLogin(ReqLoginDTO loginDTO) throws Exception;

    Customer register(Customer customer) throws Exception;

    ResLoginDTO getRefreshToken(String refresh_token) throws Exception;

    ResCustomerLoginDTO getCustomerRefreshToken(String refresh_token) throws Exception;

    ResLoginDTO.UserGetAccount getAccount() throws Exception;

    ResCustomerLoginDTO.CustomerGetAccount getCustomer() throws Exception;

    Void logout() throws Exception;

    Void logoutForCustomer() throws Exception;

    ResRegisterCustomerDTO convertToResRegisterCustomerDTO(Customer customer) throws Exception;
}
