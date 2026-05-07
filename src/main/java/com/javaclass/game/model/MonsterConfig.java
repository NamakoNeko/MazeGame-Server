package com.javaclass.game.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "monster_config")
public class MonsterConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monster_key", nullable = false, unique = true, length = 80)
    private String monsterKey;

    @Column(name = "model_path", length = 255)
    private String modelPath;

    @Column(name = "hp", nullable = false)
    private Integer hp = 20;

    @Column(name = "atk", nullable = false)
    private Integer atk = 5;

    @Column(name = "def", nullable = false)
    private Integer def = 0;

    @Column(name = "move_speed", nullable = false)
    private Double moveSpeed = 2.0;

    @Column(name = "model_scale", nullable = false)
    private Double modelScale = 1.0;
}
