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
    private String type; // WEAPON, ARMOR, POTION

    @Column(columnDefinition = "TEXT")
    private String description;

    // --- 新增屬性欄位 ---
    
    private Double attack;    // 武器攻擊力 或 藥水攻擊加成
    
    private Integer defense;  // 防具防禦力 或 藥水防禦加成
    
    private Integer hp;       // 防具生命值加成 或 藥水回復量
    
    private Integer duration; // 效果持續時間 (秒)，適用於短暫提高能力的藥水

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}