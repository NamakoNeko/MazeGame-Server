package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlayerItemResult {

    private Long playerItemId;
    private Long itemId;
    private String name;
    private String description;
    private String effect;
    private String rare;
    private Integer type;
    private String modelPath;
    private Integer location;
    private Integer position;
    private Integer amount;
    private Integer maxAmount;
    private Long buyPrice;
    private Long sellPrice;
    private List<ItemAttributeResult> attributes;
}
