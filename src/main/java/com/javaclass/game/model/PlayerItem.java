package com.javaclass.game.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "player_item")
@Data
public class PlayerItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;             // 物品實例唯一 ID

    private Long playerId;       // 屬於哪個腳色
    
    private Long itemId;         // 關聯的道具模板 ID (Item Template)

    // --- 實例化隨機屬性 ---
    // 這是後端在掉落時，從 3~8 (或其他區間) 隨機選出的固定數值
    private Double instanceAtk;  // 此件裝備的攻擊力
    private Integer instanceDef; // 此件裝備的防禦力
    private Integer instanceHp;  // 此件裝備的生命值

    // --- 狀態資訊 ---
    // location 用於判斷是在「背包」還是「倉庫」
    // 需求書：背包打開呼叫 getallitems / 昌庫打開呼叫 getallitems
    private String location;     // "BAG" (背包), "STORAGE" (倉庫)

    private boolean isEquipped;  // 是否已穿在身上

 // 預設構造：新物品產出時預設在背包
    public PlayerItem() {
        this.location = "BAG";
        this.isEquipped = false;
    }
}