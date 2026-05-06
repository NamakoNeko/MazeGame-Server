package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InventoryItemResult {

    private Long id;
    private Long itemId;
    private String itemName;
    private Integer quantity;
    private LocalDateTime updatedAt;
}