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
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "hp")
    private Integer hp;

    @Column(name = "atk")
    private Integer atk;

    @Column(name = "def")
    private Integer def;

    @Column(name = "duration")
    private Integer duration;
}