package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotkeyResult {
    private Integer keyIndex;
    private Long playerItemId;
}
