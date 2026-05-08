package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemResult {

    private Long id;
    private String name;
    private String description;
    private String effect;
    private Integer type;
    private String rare;
    private Integer maxAmount;
    private Integer hp;
    private Integer atk;
    private Integer def;
    private Integer duration;
}
