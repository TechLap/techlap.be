package com.example.techlap.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqRegisterCustomerDTO {
    @Email
    @NotBlank(message = "email isn't blank")
    private String email;

    @NotBlank(message = "password isn't blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Mật khẩu phải tối thiểu 8 ký tự, gồm ít nhất 1 chữ thường, 1 chữ hoa và 1 chữ số")
    private String password;

    @NotBlank(message = "confirmPassword isn't blank")
    private String confirmPassword;

    @NotBlank(message = "fullName isn't blank")
    private String fullName;

    @Pattern(regexp = "^(0[0-9]{9})$", message = "Invalid phone number")
    private String phone;

    @NotBlank(message = "Address isn't blank")
    private String address;
}
