package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlayerRegisterResponse {

    private Long playerId;
    private String accountId;
    private String nickname;
    private String email;
    private LocalDateTime createdAt;
}