package com.example.techlap.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqRemoveCartDetailDTO {
    private long cartDetailId;
    private long customerId;
}
