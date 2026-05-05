package com.javaclass.game.dto;

import lombok.Data;

@Data
public class InventoryItemDto {
    private Long id;            // PlayerItem 的 ID
    private String itemName;    // 從 Item 模板抓到的名字
    private Double instanceAtk;
    private Integer instanceDef;
    private Integer instanceHp;
    private String location;    // BAG, STORAGE
    private boolean equipped;   // 是否穿戴中
    private String slot;        // 部位 (HEAD, WEAPON...) 用於前端脫下判斷
}