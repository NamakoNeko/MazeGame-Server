package com.javaclass.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GainPlayerItemRequest {

    private List<GainPlayerItemEntry> items;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GainPlayerItemEntry {
        private Long itemId;
        private Integer amount;
        private Integer location;
        private Integer position;
    }
}