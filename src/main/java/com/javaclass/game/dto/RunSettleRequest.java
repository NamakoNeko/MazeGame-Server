package com.javaclass.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RunSettleRequest {
    private Boolean success;
    private Integer coinsEarned;
    private Integer elapsedSeconds;
}
