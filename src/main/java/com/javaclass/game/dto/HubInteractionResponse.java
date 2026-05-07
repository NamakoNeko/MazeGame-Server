package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HubInteractionResponse {
    private String action;
    private Boolean allowed;
    private String message;
}
