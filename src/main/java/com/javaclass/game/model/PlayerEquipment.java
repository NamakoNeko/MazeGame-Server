package com.javaclass.game.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "player_equipment")
@Data
@ToString(exclude = "player")
public class PlayerEquipment {

    @Id
    private Long playerId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "player_id")
    private Player player;

    private Long headId;
    private Long chestId;
    private Long weaponId;
    private Long offHandId;
    private Long shoesId;
}
