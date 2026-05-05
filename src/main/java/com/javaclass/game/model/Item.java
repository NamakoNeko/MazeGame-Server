package com.javaclass.game.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "item")
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String type;        // WEAPON, ARMOR, POTION

    @Column(length = 50)
    private String slot;        // HEAD (頭), CHEST (甲), WEAPON (主武器), OFF_HAND (副手), SHOES (鞋), NONE (無)

    @Column(length = 20)
    private String rarity;      // WHITE (白), BLUE (藍), YELLOW (黃)

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer price;
    // --- 模板基礎屬性 (可用作基礎加成或藥水效果) ---
    private Integer attack;      // 基礎攻擊力
    private Integer defense;    // 基礎防禦力
    private Integer hp;         // 基礎生命值
    private Integer duration;   // 藥水效果持續時間 (秒)

    @Column(name = "is_deleted")
    private boolean isDeleted = false; // 🔥 補上這個，軟刪除才有用

    @Column(name = "create_time", updatable = false) // 🔥 改成 create_time
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}