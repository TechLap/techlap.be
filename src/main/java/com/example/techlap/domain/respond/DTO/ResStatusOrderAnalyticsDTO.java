package com.example.techlap.domain.respond.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResStatusOrderAnalyticsDTO {
    private int delivered;
    private int processing;
    private int pending;
    private int paid;
    private int shipping;
    private int cancelled;
}
