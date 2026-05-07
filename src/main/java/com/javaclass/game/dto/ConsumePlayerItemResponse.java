package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ConsumePlayerItemResponse {

    private List<PlayerItemEntry> items;

    @Getter
    @Builder
    public static class PlayerItemEntry {
        private Long itemId;
        private Integer amount;
        private Integer location;
        private Integer position;
    }
}