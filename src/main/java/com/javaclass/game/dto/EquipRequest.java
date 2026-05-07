package com.javaclass.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EquipRequest {

    private Long itemId;
    private Integer location;
    private Integer position;
}