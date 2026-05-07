package com.javaclass.game.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "hotkey",
    uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "key_index"})
)
public class Hotkey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "key_index", nullable = false)
    private Integer keyIndex;

    @Column(name = "player_item_id")
    private Long playerItemId;
}
