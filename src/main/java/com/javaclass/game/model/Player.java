package com.javaclass.game.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "player")
@Data
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountId; // 帳號關聯

    private String nickname;
    private Long money = 0L;

    // 級聯操作：當 Player 被儲存/刪除時，連帶處理 Stats 和 Equipment
    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PlayerStats stats;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PlayerEquipment equipment;
}