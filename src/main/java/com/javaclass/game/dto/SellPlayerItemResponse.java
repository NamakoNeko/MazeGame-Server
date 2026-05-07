package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SellPlayerItemResponse {
    private Long playerItemId;
    private Long itemId;
    private Integer soldAmount;
    private Integer remainingAmount;
    private Long gainedMoney;
    private Long money;
}
