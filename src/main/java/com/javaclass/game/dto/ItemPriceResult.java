package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemPriceResult {
    private Long itemId;
    private Long buyPrice;
    private Long sellPrice;
}
