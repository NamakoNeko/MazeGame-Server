package com.javaclass.game.dao;

import com.javaclass.game.model.ItemPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPriceDao extends JpaRepository<ItemPrice, Long> {
}
