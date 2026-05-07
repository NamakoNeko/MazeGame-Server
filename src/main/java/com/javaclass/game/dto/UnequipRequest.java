package com.javaclass.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnequipRequest {

    private Long itemId;
    private Integer targetLocation;
    private Integer targetPosition;
}