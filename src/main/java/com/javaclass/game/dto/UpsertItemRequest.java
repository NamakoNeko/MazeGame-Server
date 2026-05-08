package com.javaclass.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpsertItemRequest {

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
