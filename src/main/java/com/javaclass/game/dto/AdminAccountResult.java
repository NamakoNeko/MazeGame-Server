package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminAccountResult {

    private Long id;
    private String account;
    private String role;
    private LocalDateTime createdAt;
}
