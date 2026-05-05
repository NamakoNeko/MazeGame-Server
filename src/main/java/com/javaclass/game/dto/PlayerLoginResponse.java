package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlayerLoginResponse {

    private String token;
    private Long playerId;
    private String accountId;
    private String nickname;
    private Long money;
    private LocalDateTime expiresAt;
}