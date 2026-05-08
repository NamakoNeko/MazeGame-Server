package com.javaclass.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReplaceLocationItemsRequest {

    private List<ReplaceLocationItemEntry> items;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReplaceLocationItemEntry {
        private Long itemId;
        private Integer amount;
        private Integer position;
    }
}
