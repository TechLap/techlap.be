package com.example.techlap.service;

import com.example.techlap.domain.User;
import com.example.techlap.domain.request.ReqLoginDTO;
import com.example.techlap.domain.respond.DTO.ResLoginDTO;

public interface AuthService {

    ResLoginDTO login(ReqLoginDTO loginDTO) throws Exception;

    User register(User user) throws Exception;

}
