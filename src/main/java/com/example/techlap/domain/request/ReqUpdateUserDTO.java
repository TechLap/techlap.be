package com.example.techlap.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateUserDTO {
    private int id;

    @NotBlank(message = "Full Name must not be blank!")
    private String fullName;

    @NotBlank(message = "Address must not be blank!")
    private String address;

    @NotBlank(message = "Phone must not be blank!")
    @Pattern(regexp = "^(0[35789])(\\d{8})$", message = "Invalid phone number!")
    private String phone;
}