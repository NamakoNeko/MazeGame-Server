package com.javaclass.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoveItemRequest {

    private Long itemId;
    private Integer quantity;
    private String reason;
}