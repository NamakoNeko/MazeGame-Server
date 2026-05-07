package com.javaclass.game.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "player_items")
public class PlayerItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "location", nullable = false)
    private Integer location;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "amount", nullable = false)
    private Integer amount;
}
