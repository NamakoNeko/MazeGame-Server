package com.javaclass.game.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OperationLogResult {

    private Long id;
    private Long adminId;
    private String adminAccount;
    private String action;
    private String targetType;
    private String targetId;
    private String detail;
    private LocalDateTime createdAt;
}
