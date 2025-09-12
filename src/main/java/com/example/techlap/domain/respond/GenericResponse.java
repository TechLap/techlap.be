package com.example.techlap.domain.respond;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericResponse {
    private String message;
    private String error;

    public GenericResponse(String message) {
        super();
        this.message = message;
    }

    public GenericResponse(String message, String error) {
        super();
        this.message = message;
        this.error = error;
    }
}