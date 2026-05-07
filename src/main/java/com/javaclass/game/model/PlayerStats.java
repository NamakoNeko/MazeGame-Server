package com.javaclass.game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "player_stats")
@Data
@ToString(exclude = "player")
public class PlayerStats {
    @Id
    private Long playerId; // 與 Player 共用 ID

    @OneToOne
    @MapsId	// 讓此表的 PK 等於 Player 表的 PK
    @JoinColumn(name = "player_id")
    @JsonIgnore
    private Player player;

    private Integer hp = 100;
    private Double atk = 10.0;
    private Integer def = 5;
    private Long money = 0L;
}
