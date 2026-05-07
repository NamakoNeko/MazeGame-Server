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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false, unique = true, length = 50)
    private String accountId;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "account_id", insertable = false, updatable = false)
    private Player player;

    private Long headId;
    private Long chestId;
    private Long weaponId;
    private Long offHandId;
    private Long shoesId;
}