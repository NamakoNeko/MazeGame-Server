package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlayerListResult {

    private Long id;
    private String accountId;
    private String nickname;
    private Integer level;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}