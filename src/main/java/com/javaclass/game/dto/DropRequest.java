package com.javaclass.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DropRequest {
    private String source;
    private Integer amount;
    private Integer targetLocation;
    private Integer targetPosition;
}
