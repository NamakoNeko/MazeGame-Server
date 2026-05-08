package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EquipmentResponse {
    private Long playerId;
    private Long headId;
    private Long chestId;
    private Long weaponId;
    private Long offHandId;
    private Long shoesId;
}
