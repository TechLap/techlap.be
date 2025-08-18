package com.example.techlap.domain.respond;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResPagination {
    private Meta meta;
    private Object result;

    @Setter
    @Getter
    public static class Meta {
        private int page;
        private int pageSize;
        private int pages;
        private long total;
    }
}