package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonsterConfigResult {
    private Long id;
    private String monsterKey;
    private String modelPath;
    private Integer hp;
    private Integer atk;
    private Integer def;
    private Double moveSpeed;
    private Double modelScale;
}
