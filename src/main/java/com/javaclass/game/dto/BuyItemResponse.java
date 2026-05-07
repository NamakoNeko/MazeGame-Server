package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BuyItemResponse {
    private Long itemId;
    private Integer amount;
    private Integer location;
    private Integer position;
    private Long spentMoney;
    private Long money;
}
