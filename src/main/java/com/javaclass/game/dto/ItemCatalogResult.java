package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ItemCatalogResult {
    private Long itemId;
    private String name;
    private String description;
    private Integer type;
    private String rare;
    private Integer maxAmount;
    private String modelPath;
    private Long buyPrice;
    private Long sellPrice;
    private List<ItemAttributeResult> attributes;
}
