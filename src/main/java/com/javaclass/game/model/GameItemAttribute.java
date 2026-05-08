package com.javaclass.game.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "game_item_attribute")
public class GameItemAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "effect_type", nullable = false, length = 50)
    private String effectType;

    @Column(name = "value", nullable = false)
    private Integer value;

    @Column(name = "duration")
    private Integer duration;
}
