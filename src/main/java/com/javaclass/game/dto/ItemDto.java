package com.javaclass.game.dto;

import lombok.Data;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String type;
    private String description;
    private String createdAt; // 轉為字串方便前端顯示
}