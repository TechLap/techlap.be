package com.example.techlap.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriteriaFilterPermission {

    private String name;
    private String apiPath;
    private String method;
    private String module;
    private String createdAt;

}