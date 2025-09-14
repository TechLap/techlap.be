package com.example.techlap.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqAddToCartDTO {
    private long productId;
    private int quantity;
}
