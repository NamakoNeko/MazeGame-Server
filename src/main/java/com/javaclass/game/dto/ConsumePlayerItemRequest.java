package com.javaclass.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ConsumePlayerItemRequest {

    private List<ConsumePlayerItemEntry> items;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ConsumePlayerItemEntry {
        private Long itemId;
        private Integer amount;
        private Integer location;
        private Integer position;
    }
}