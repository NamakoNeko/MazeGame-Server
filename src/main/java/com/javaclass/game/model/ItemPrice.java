package com.javaclass.game.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "item_price")
public class ItemPrice {

    @Id
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "buy_price", nullable = false)
    private Long buyPrice = 0L;

    @Column(name = "sell_price", nullable = false)
    private Long sellPrice = 0L;
}
