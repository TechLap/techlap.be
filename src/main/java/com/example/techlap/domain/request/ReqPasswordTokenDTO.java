package com.example.techlap.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqPasswordTokenDTO {

    private String oldPassword;

    private String token;

    @NotBlank(message = "password isn't blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Mật khẩu phải tối thiểu 8 ký tự, gồm ít nhất 1 chữ thường, 1 chữ hoa và 1 chữ số")
    private String newPassword;
}