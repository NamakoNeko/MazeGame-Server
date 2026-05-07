package com.javaclass.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BuyItemRequest {
    private Long itemId;
    private Integer amount;
    private Integer location;
    private Integer position;
}
