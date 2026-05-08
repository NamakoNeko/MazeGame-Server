package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemAttributeResult {
    private String effectType;
    private Integer value;
    private Integer duration;
}
