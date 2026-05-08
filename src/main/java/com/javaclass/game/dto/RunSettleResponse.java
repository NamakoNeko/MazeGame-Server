package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RunSettleResponse {
    private Boolean success;
    private Integer coinsEarned;
    private Integer elapsedSeconds;
    private Long money;
}
