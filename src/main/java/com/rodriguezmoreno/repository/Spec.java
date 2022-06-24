package com.rodriguezmoreno.repository;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Spec {
    private Long sourceId;
    private LocalDateTime ts1;
    private LocalDateTime ts2;
    private Long min;
    private Long max;
}
