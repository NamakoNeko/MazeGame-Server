package com.javaclass.game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "player_equipment")
@Data
@ToString(exclude = "player")
public class PlayerEquipment {
    @Id
    private Long playerId; // 與 Player 共用 ID

    @OneToOne
    @MapsId
    @JoinColumn(name = "player_id")
    @JsonIgnore
    private Player player;

    private Long headId;
    private Long chestId;
    private Long weaponId;
    private Long offHandId;
    private Long shoesId;
}