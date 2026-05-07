package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovePlayerItemResponse {

    private Long itemId;
    private Integer amount;
    private Integer beforeLocation;
    private Integer beforePosition;
    private Integer afterLocation;
    private Integer afterPosition;
}